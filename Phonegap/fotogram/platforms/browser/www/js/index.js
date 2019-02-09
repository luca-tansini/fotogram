$(document).ready(docReady);

function docReady(){

    /*let user1 = new LoggedUser("froggo","img/rana_xs.jpg","sid42");
    let user2 = new User("doggo","img/cane_xs.jpg");
    let user3 = new User("bojack","img/cavallo_xs.jpg");
    user1.setFollowing([user2,user3]);

    model.getInstance().setHomeWall([new Post(user1,"img/pastry_xs.jpg","pasticcini"), new Post(user2,"img/arance_xs.jpg","arance"), new Post(user1,"img/palloncini_xs.jpg","palloncini"), new Post(user3,"img/chitarra_xs.jpg","guitar")]);
    model.getInstance().setLoggedUser(user1);*/

    $(".page").hide();
    $("#bottomNavigation").hide();
    $("#loginPage").show();
    $("#loginButton").on("click", loginButtonClick);
    $("#navHome").on("click",home);
    $("#navUpload").on("click",upload);
    $("#navSearchUser").on("click",searchUser);
    $("#navMyProfile").on("click",myProfile);
    $("#uploadImageButton").on("click",uploadImage);
    $(document).on("click",".home-post", homePostListener);
}

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

// mostra il bottone indietro e gli assegna il comportamento
// alla fine del comportamento cancella l'handler e nasconde il bottone
function showBackButton(logic){
    $("#backButton").show();
    $("#toolbarTitle").css({marginLeft: "-75px"});
    $("#backButton").on("click", function(){
        logic();
        $(document).off("click", "#backButton");
        hideBackButton();
    })
}

function hideBackButton(){
    $("#backButton").hide();
    $("#toolbarTitle").css({marginLeft: "0px"});
}

function home(){
    $(".page").hide();
    $("#home").show();
    $("#wall").empty();

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
            console.log(following);
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
    let html = '<li data-username="'+post.user.uid+'" class="home-post list-group-item pb-0 pt-2">\n<div style="height: 48px" class="row border-bottom pb-2 col-12 px-0">\n<img class="profile-picture" src="';
    html += base64toSrc(post.user.profilePicture);
    html += '"/>\n<p id="username" class="col-8 pt-2">';
    html += post.user.uid;
    html += '</p>\n</div>\n<div class="row">\n<img class="col-12 px-0 h-100" src="'
    html += base64toSrc(post.picture);
    html += '" id="postPicture"/>\n</div>\n<div class="row">\n<p id="description" class="col-12 px-0" style="font-size:14px">'
    html += post.description;
    html += '</p>\n</div>\n</li>'
    return html;
}

function base64toSrc(imgBase64){
    if(imgBase64.substring(0,10) != "data:image")
        return 'data:image/jpeg;base64,'+imgBase64;
    return imgBase64;
}

function homePostListener(){
    let username = $(this).data("username");
    console.log(username);
    //Se il post è dell'utente loggato va a myProfile
    if(model.getInstance().getLoggedUser().uid == username){
        myProfile();
    }
    //Altrimenti va a userProfile
    else{
        userProfile(username);
    }
}

function upload(){
    $(".page").hide();
    $("#upload").show();
}

function searchUser(){
    $(".page").hide();
    $("#searchUser").show();
}

function myProfile(){
    $(".page").hide();
    $("#myProfile").show();
    $("#myProfileWall").empty();
    let html = '<li class=border-bottom> <div id="profileHeader" class="row col-12 pb-3"> <img id="profilePicture" src="';
    html += base64toSrc(model.getInstance().getLoggedUser().profilePicture) + '" class="profile-picture ml-3 mt-3" style="height:100px; width:100px;"/> <div class="d-flex align-items-center justify-content-center col-8 flex-column pt-3">';
    html += '<p>'+model.getInstance().getLoggedUser().uid+'</p>';
    html += '<button class="btn btn-primary" style="background-color: red; color: black; border: none; width: 100px; height: 30px;">logout</button> </div> </div> </li>';
    $("#myProfileWall").append(html);
    for(post of model.getInstance().getHomeWall()){
        if(post.user.uid == model.getInstance().getLoggedUser().uid)
            $("#myProfileWall").append(makeProfilePost(post));
    }
}

function makeProfilePost(post){
    let html = '<li data-username="'+post.user.uid+'" class="list-group-item pb-0 pt-2">\n<div style="height: 48px" class="row border-bottom pb-2 col-12 px-0">\n<img class="profile-picture" src="';
    html += base64toSrc(post.user.profilePicture);
    html += '"/>\n<p id="username" class="col-8 pt-2">';
    html += post.user.uid;
    html += '</p>\n</div>\n<div class="row">\n<img class="col-12 px-0 h-100" src="'
    html += base64toSrc(post.picture);
    html += '" id="postPicture"/>\n</div>\n<div class="row">\n<p id="description" class="col-12 px-0" style="font-size:14px">'
    html += post.description;
    html += '</p>\n</div>\n</li>'
    return html;
}

function uploadImage(){
    navigator.camera.getPicture(onSuccess, onFail, { quality: 25,
    destinationType: Camera.DestinationType.DATA_URL, sourceType: Camera.PictureSourceType.SAVEDPHOTOALBUM
    });
}

function onSuccess(imageData) {
    $("#img").attr("src","data:image/jpeg;base64," + imageData);
}

function onFail(message) {
    console.log('uploadImage failed because: ' + message);
}

function userProfile(username){
    $(".page").hide();
    $("#userProfile").show();
    showBackButton(function(){
        home();
    });
    $("#userProfileWall").empty();
    let user = undefined;
    for(u of model.getInstance().getLoggedUser().getFollowing()){
        if (u.uid == username) {
            user = u;
            break;
        }
    }
    let html = '<li class=border-bottom> <div id="profileHeader" class="row col-12 pb-3"> <img id="profilePicture" src="';
    html += base64toSrc(user.profilePicture) + '" class="profile-picture ml-3 mt-3" style="height:100px; width:100px;"/> <div class="d-flex align-items-center justify-content-center col-8 flex-column pt-3">';
    html += '<p>'+user.uid+'</p>';
    html += '<button class="btn btn-primary" style="background-color: red; color: black; border: none; width: 100px; height: 30px;">unfollow</button> </div> </div> </li>';
    $("#userProfileWall").append(html);
    for(post of model.getInstance().getHomeWall()){
        if(post.user.uid == user.uid)
            $("#userProfileWall").append(makeProfilePost(post));
    }
}
