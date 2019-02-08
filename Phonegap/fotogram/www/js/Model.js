class User{
    constructor(uid, profilePicture=undefined){
        this._uid = uid;
        this._profilePicture = profilePicture;
    }

    get uid(){
        return this._uid;
    }

    get profilePicture(){
        return this._profilePicture;
    }
}

class Post{
    constructor(user, picture, description){
        this._user = user;
        this._picture = picture;
        this._description = description;
    }

    get user(){
        return this._user;
    }

    get picture(){
        return this._picture;
    }

    get description(){
        return this._description;
    }
}

var user1 = new User("froggo","img/rana_xs.jpg");
var user2 = new User("doggo","img/cane_xs.jpg");
var user3 = new User("bojack","img/cavallo_xs.jpg");

var model = [new Post(user1,"img/pastry_xs.jpg","pasticcini"), new Post(user2,"img/arance_xs.jpg","arance"), new Post(user1,"img/palloncini_xs.jpg","palloncini"), new Post(user3,"img/chitarra_xs.jpg","guitar")];
