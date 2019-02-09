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

    set profilePicture(profilePicture){
        this._profilePicture = profilePicture;
    }

}

class LoggedUser extends User {
    constructor(uid, profilePicture=undefined,sessionid) {
        super(uid,profilePicture);
        this._sessionid = sessionid;
    }

    get sessionid(){
        return this._sessionid;
    }

    setFollowing(following){
        this._following = following;
    }

    getFollowing(){
        return this._following;
    }

}

class Post{
    constructor(user, picture, description, timestamp){
        this._user = user;
        this._picture = picture;
        this._description = description;
        this._timestamp = timestamp;
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

    get timestamp(){
        return this._timestamp;
    }

}

class Model{

    constructor(){}

    getLoggedUser(){
        return this._loggedUser;
    }

    setLoggedUser(loggedUser){
        this._loggedUser = loggedUser;
    }

}

var model = (function(){
	var instance;
	return {
		getInstance: function() {
			if (!instance) {
				instance = new Model();
			}
			return instance;
		}
	};
})();
