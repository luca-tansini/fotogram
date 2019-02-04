$(document).ready(docReady);

function docReady(){
    $(".page").hide();
    $("#bottomNavigation").hide();
    $("#loginPage").show();
    $("#loginButton").on("click", loginButtonClick);
    $("#navHome").on("click",navClosure("home"));
    $("#navSearchUser").on("click",navClosure("searchUser"));
    $("#navMyProfile").on("click",navClosure("myProfile"));
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
                $(".page").hide();
                $("#home").show();
                $("#bottomNavigation").show();
                console.log(resultData);
            },
            error: function(error){
                $("#errorText").html("invalid username or password");
            }
        });
    }
}

function navClosure(pageid){
    return function() {
        $(".page").hide();
        $("#"+pageid).show();
    }
}
