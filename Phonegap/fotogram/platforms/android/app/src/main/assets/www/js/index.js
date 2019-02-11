$(document).ready(docReady);

function docReady(){
    $(".page").hide();
    $("#bottomNavigation").hide();
    $("#loginButton").on("click", loginButtonClick);

    $("#navHome").on("click",function(){
        hideBackButton();
        $(".tanso-nav-link").css("color","white");
        $("#homeNavLink").css("color","#007bff");
        home();
    });

    $("#navUpload").on("click",function(){
        hideBackButton();
        $(".tanso-nav-link").css("color","white");
        $("#uploadNavLink").css("color","#007bff");
        upload();
    });
    $("#uploadImageButton").on("click",uploadImage);

    $("#navSearchUser").on("click",function(){
        hideBackButton();
        $(".tanso-nav-link").css("color","white");
        $("#searchUserNavLink").css("color","#007bff");
        searchUser();
    });
    $("#inputSearchUser").on("input",inputSearchUser);
    $(document).on("click",".search-user-list-item", searchUserListener);

    $("#navMyProfile").on("click",function(){
        hideBackButton();
        $(".tanso-nav-link").css("color","white");
        $("#myProfileNavLink").css("color","#007bff");
        myProfile();
    });
    $(document).on("click",".home-post", homePostListener);
    $(document).on("click","#logoutButton", logoutButtonClick);

    $(document).on("click","#followButton", followButtonClick);
    $(document).on("click","#unfollowButton", unfollowButtonClick);

    if(localStorage.getItem("username") && localStorage.getItem("sessionid")){
        let username = localStorage.getItem("username");
        let sessionid = localStorage.getItem("sessionid");

        //Expired sessionid check
        let myData = { 'session_id': sessionid};
        $.ajax({
            type: 'POST',
            url: "https://ewserver.di.unimi.it/mobicomp/fotogram/followed",
            data: myData,
            success: function(resultData){
                model.getInstance().setLoggedUser(new LoggedUser(username,undefined,sessionid));
                console.log(sessionid);
                $("#bottomNavigation").show();
                home();
            },
            error: function(error){
                localStorage.removeItem("sessionid");
                localStorage.removeItem("username");
                $("#loginPage").show();
                $("#errorText").html("session expired");
            }
        });
    }
    else{
        $("#loginPage").show();
    }
}

/************************************LOGIN*************************************/

function loginButtonClick(){
    $("#errorText").html("");
    let username = $("#inputUsername").val();
    let password = $("#inputPassword").val();
    if(username == "" || password == ""){
        $("#errorText").html("fill in all fields");
    } else{
        let myData = { 'username': username, 'password': password};
        $.ajax({
            type: 'POST',
            url: "https://ewserver.di.unimi.it/mobicomp/fotogram/login",
            data: myData,
            success: function(resultData){
                localStorage.setItem("username",username);
                localStorage.setItem("sessionid",resultData);
                model.getInstance().setLoggedUser(new LoggedUser(username,undefined,resultData));
                console.log(resultData);
                $("#bottomNavigation").show();
                home();
            },
            error: function(error){
                $("#errorText").html("invalid username or password");
            }
        });
    }
}

function clearLoginPage(){
    $("#inputUsername").val("");
    $("#inputPassword").val("");
}

/*************************************HOME*************************************/

function home(){
    $(".page").hide();
    $("#home").show();
    $("#wall").empty();
    $("#homeLoader").show();

    //Followed REST API call
    let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid};
    $.ajax({
        type: 'POST',
        url: "https://ewserver.di.unimi.it/mobicomp/fotogram/followed",
        data: myData,
        success: function(resultData){
            json = JSON.parse(resultData);
            let following = [];
            for(user of json.followed){
                if(user.name == model.getInstance().getLoggedUser().uid)
                    model.getInstance().getLoggedUser().profilePicture = user.picture;
                else
                    following[user.name] = new User(user.name,user.picture);
            }
            model.getInstance().getLoggedUser().setFollowing(following);
            showWall();
        },
        error: function(error){
            console.log("error in followed call: "+error);
        }
    });
}

function showWall(){
    let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid};
    $.ajax({
        type: 'POST',
        url: "https://ewserver.di.unimi.it/mobicomp/fotogram/wall",
        data: myData,
        success: function(resultData){
            json = JSON.parse(resultData);
            let wall = [];
            for(post of json.posts){
                if(post.user == model.getInstance().getLoggedUser().uid)
                    wall.push(new Post(model.getInstance().getLoggedUser(), post.img, post.msg, post.timestamp));
                else
                    wall.push(new Post(model.getInstance().getLoggedUser().getFollowing()[post.user], post.img, post.msg, post.timestamp));
            }
            $("#homeLoader").hide();
            for(post of wall){
                $("#wall").append(makeHomePost(post));
            }
        },
        error: function(error){
            console.log("error in followed call: "+error);
        }
    });
}

//TODO: manca timestamp
function makeHomePost(post){
    let html = '<li data-username="'+post.user.uid+'" class="home-post list-group-item pb-0 pt-2">\n<div style="height: 48px" class="row border-bottom pb-2 px-0">\n<img class="profile-picture" src="';
    html += base64toSrc(post.user.profilePicture);
    html += '"/>\n<p class="col-8 pt-2">';
    html += post.user.uid;
    html += '</p>\n</div>\n<div class="row">\n<img class="col-12 px-0 h-100" src="'
    html += base64toSrc(post.picture);
    html += '"/>\n</div>\n<div class="row">\n<p class="col-12 px-0" style="font-size:14px">'
    html += post.description;
    html += '</p>\n</div>\n</li>'
    return html;
}

function homePostListener(){
    let username = $(this).data("username");
    //Se il post è dell'utente loggato va a myProfile
    if(model.getInstance().getLoggedUser().uid == username){
        myProfile();
    }
    //Altrimenti va a userProfile
    else{
        showBackButton(function(){
            home();
        });
        userProfile(username);
    }
}

function clearHomePage(){
    $("#wall").empty();
}

/************************************UPLOAD************************************/

function upload(){
    $(".page").hide();
    $("#upload").show();
}

function uploadImage(){
    navigator.camera.getPicture(onSuccess, onFail, { quality: 25,
    destinationType: Camera.DestinationType.DATA_URL, sourceType: Camera.PictureSourceType.SAVEDPHOTOALBUM
    });
}

function onSuccess(imageData) {
    $("#uploadImg").attr("src","data:image/jpeg;base64," + imageData);
}

function onFail(message) {
    console.log('uploadImage failed because: ' + message);
}

function clearUploadPage(){
    $("#uploadImg").removeAttr("src");
}

/*********************************SEARCH USER**********************************/

function searchUser(){
    $(".page").hide();
    $("#searchUser").show();
}

function inputSearchUser(){
    let prefix = $(this).val();
    $("#searchUserList").empty();
    if(prefix != ""){
        let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid, 'usernamestart':prefix};
        $.ajax({
            type: 'POST',
            url: "https://ewserver.di.unimi.it/mobicomp/fotogram/users",
            data: myData,
            success: function(resultData){
                json = JSON.parse(resultData);
                for(user of json.users){
                    let html = '<li data-username="'+user.name+'" class="search-user-list-item list-group-item pb-0 pt-2">\n<div style="height: 48px" class="row border-bottom pb-2 px-0">\n<img class="profile-picture" src="';
                    if(!user.picture || user.picture == ""){
                        html += 'img/user.png';
                    }
                    else{
                        html += base64toSrc(user.picture);
                    }
                    html += '"/>\n<p class="col-8 pt-2">';
                    html += user.name;
                    html += '</p>\n</div>\n</li>'
                    $("#searchUserList").append(html);
                }
            },
            error: function(error){
                console.log("error in profile call: "+error);
            }
        });
    }
}

function searchUserListener(){
    let username = $(this).data("username");
    if(username == model.getInstance().getLoggedUser().uid){
        myProfile();
    }
    else{
        showBackButton(function(){
            searchUser();
        });
        userProfile(username);
    }
}

function clearSearchUserPage(){
    $("#inputSearchUser").val("");
    $("#searchUserList").empty();
}

/**************************MY PROFILE & USER PROFILE***************************/

function profileAPICall(username, onSuccessLogic){
    let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid, 'username':username};
    $.ajax({
        type: 'POST',
        url: "https://ewserver.di.unimi.it/mobicomp/fotogram/profile",
        data: myData,
        success: function(resultData){
            json = JSON.parse(resultData);
            let user = new User(json.username, json.img);
            let userwall = [];
            for(post of json.posts){
                userwall.push(new Post(user, post.img, post.msg, post.timestamp));
            }
            let profileData = {'user':user, 'userwall':userwall};
            onSuccessLogic(profileData);
        },
        error: function(error){
            console.log("error in profile call: "+error);
        }
    });
}

function myProfile(){
    $(".page").hide();
    $("#myProfile").show();
    $("#myProfileWall").empty();
    $("#myProfileLoader").show();
    profileAPICall(model.getInstance().getLoggedUser().uid,
        function(profileData){
            let latestProfilePic = profileData.user.profilePicture;
            model.getInstance().getLoggedUser().profilePicture = latestProfilePic;
            let html = '<li class=border-bottom> <div class="row col-12 pb-3"> <img id="myProfilePicture" src="';
            html += base64toSrc(model.getInstance().getLoggedUser().profilePicture) + '" class="profile-picture ml-3 mt-3" style="height:100px; width:100px;"/> <div style="flex:1;"></div> <div class="d-flex align-items-center justify-content-center flex-column pt-3">';
            html += '<p>'+model.getInstance().getLoggedUser().uid+'</p>';
            html += '<button id="logoutButton" class="btn btn-primary" style="background-color: red; color: black; border: none; width: 100px; height: 30px;">logout</button> </div> <div style="flex:1;"></div> </div> </li>';
            $("#myProfileWall").append(html);
            $("#myProfileLoader").hide();
            for(post of profileData.userwall){
                $("#myProfileWall").append(makeProfilePost(post));
            }
        });
}

function makeProfilePost(post){
    let html = '<li data-username="'+post.user.uid+'" class="list-group-item pb-0 pt-2">\n<div style="height: 48px" class="row border-bottom pb-2 px-0">\n<img class="profile-picture" src="';
    html += base64toSrc(post.user.profilePicture);
    html += '"/>\n<p class="col-8 pt-2">';
    html += post.user.uid;
    html += '</p>\n</div>\n<div class="row">\n<img class="col-12 px-0 h-100" src="'
    html += base64toSrc(post.picture);
    html += '"/>\n</div>\n<div class="row">\n<p class="col-12 px-0" style="font-size:14px">'
    html += post.description;
    html += '</p>\n</div>\n</li>'
    return html;
}

function logoutButtonClick(){
    let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid};
    $.ajax({
        type: 'POST',
        url: "https://ewserver.di.unimi.it/mobicomp/fotogram/logout",
        data: myData,
        success: function(resultData){
            //TODO: persistenza
            model.instance = undefined;
            localStorage.removeItem("sessionid");
            localStorage.removeItem("username");
            $(".page").hide();
            $("#bottomNavigation").hide();
            clearAllPages();
            $("#loginPage").show();
        },
        error: function(error){
            console.log("error in logout call: "+error);
        }
    });
}

function userProfile(username){
    $(".page").hide();
    $("#userProfile").show();
    $("#userProfileWall").empty();
    $("#userProfileLoader").show();

    profileAPICall(username,
        function(profileData){
            let html = '<li class=border-bottom> <div class="row col-12 pb-3"> <img id="userProfilePicture" src="';
            html += base64toSrc(profileData.user.profilePicture) + '" class="profile-picture ml-3 mt-3" style="height:100px; width:100px;"/> <div style="flex:1;"></div> <div class="d-flex align-items-center justify-content-center flex-column pt-3">';
            html += '<p>'+username+'</p>';
            if(model.getInstance().getLoggedUser().getFollowing()[username])
                html += '<button id="unfollowButton" data-username="'+username+'" class="btn btn-primary" style="background-color: red; color: black; border: none; width: 100px; height: 30px;">unfollow</button> </div> <div style="flex:1;"> </div> </li>';
            else
                html += '<button id="followButton" data-username="'+username+'" class="btn btn-primary" style="background-color: #007bff; color: black; border: none; width: 100px; height: 30px;">follow</button> </div> <div style="flex:1;"> </div> </li>';
            $("#userProfileWall").append(html);
            $("#userProfileLoader").hide();
            for(post of profileData.userwall){
                $("#userProfileWall").append(makeProfilePost(post));
            }
        });
}

function followButtonClick(){
    let username = $(this).data("username");
    let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid, 'username':username};
    $.ajax({
        type: 'POST',
        url: "https://ewserver.di.unimi.it/mobicomp/fotogram/follow",
        data: myData,
        success: function(resultData){
            let pic = $("#userProfilePicture").attr("src");
            model.getInstance().getLoggedUser().getFollowing()[username] = new User(username,pic);
            userProfile(username);
        },
        error: function(error){
            console.log("error in logout call: "+error);
        }
    });
}

function unfollowButtonClick(){
    let username = $(this).data("username");
    let myData = { 'session_id': model.getInstance().getLoggedUser().sessionid, 'username':username};
    $.ajax({
        type: 'POST',
        url: "https://ewserver.di.unimi.it/mobicomp/fotogram/unfollow",
        data: myData,
        success: function(resultData){
            delete model.getInstance().getLoggedUser().getFollowing()[username];
            userProfile(username);
        },
        error: function(error){
            console.log("error in logout call: "+error);
        }
    });
}

/********************************MISCELLANEOUS*********************************/

// mostra il bottone indietro e gli assegna il comportamento
// alla fine del comportamento cancella l'handler e nasconde il bottone
function showBackButton(logic){
    $("#backButton").show();
    $("#toolbarTitle").css({marginLeft: "-75px"});
    $('#backButton').off("click");
    $("#backButton").on("click", function(){
        logic();
        hideBackButton();
    });
}

function hideBackButton(){
    $('#backButton').off("click");
    $("#backButton").hide();
    $("#toolbarTitle").css({marginLeft: "0px"});
}

function clearAllPages(){
    clearLoginPage();
    clearHomePage();
    clearUploadPage();
    clearSearchUserPage();
    $(".tanso-nav-link").css("color","white");
    $("#homeNavLink").css("color","#007bff");
}

function base64toSrc(imgBase64){
    if(imgBase64.substring(0,10) != "data:image")
        return 'data:image/jpeg;base64,'+imgBase64;
    return imgBase64;
}
