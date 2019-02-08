$(document).ready(docReady);

function docReady(){
    $(".page").hide();
    $("#bottomNavigation").hide();
    $("#loginPage").show();
    $("#loginButton").on("click", loginButtonClick);
    $("#navHome").on("click",navClosure("home"));
    $("#navUpload").on("click",navClosure("upload"));
    $("#navSearchUser").on("click",navClosure("searchUser"));
    $("#navMyProfile").on("click",navClosure("myProfile"));
    for(post of model){
        $("#wall").append(makeHtmlPost(post));
    }
    $("#uploadImageButton").on("click",uploadImage);
}

function loginButtonClick(){
    $("#errorText").html("");
    let username = $("#inputUsername").val();
    let password = $("#inputPassword").val();
    if(username == "" || password == ""){
        $("#errorText").html("fill in all fields");
    } else{
        /*let myData = { 'username': username, 'password': password};
        $.ajax({
            type: 'POST',
            url: "https://ewserver.di.unimi.it/mobicomp/fotogram/login",
            data: myData,
            success: function(resultData){
                $(".page").hide();
                $("#home").show();
                $("#bottomNavigation").show();
                console.log(resultData);
            },
            error: function(error){
                $("#errorText").html("invalid username or password");
            }
        });*/
        $(".page").hide();
        $("#home").show();
        $("#bottomNavigation").show();
    }
}

function navClosure(pageid){
    return function() {
        $(".page").hide();
        $("#"+pageid).show();
    }
}

function makeHtmlPost(post){
    html = '<li class="list-group-item pb-0 pt-2">\n<div style="height: 48px" class="row border-bottom pb-2 col-12 px-0">\n<img class="profile-picture" src="';
    html += post.user.profilePicture;
    html += '" id="profilePicture"/>\n<p id="username" class="col-8 pt-2">';
    html += post.user.uid;
    html += '</p>\n</div>\n<div class="row">\n<img class="col-12 px-0 h-100" src="'
    html += post.picture;
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
