package com.chatapp.example.flamingoapp.models;

public class Users {

    String profilepic;
    String userName;
    String mail;
    String password;  String userId;
    String lastMessage;
    String status;
    String phoneNumber;
    String statusImage;
    String userBio;
    String userLink;
    String fullName;
    String followers;
    String following;
    String posts;

    public String getSmartReply() {
        return smartReply;
    }

    public void setSmartReply(String smartReply) {
        this.smartReply = smartReply;
    }

    String smartReply;


    public Users(String profilepic, String userName, String mail, String password, String userId, String lastMessage, String status, String phoneNumber, String statusImage, String userBio) {
        this.profilepic = profilepic;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.statusImage = statusImage;
        this.userBio = userBio;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public Users(String userName, String mail, String password, String phoneNumber, String fullName) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Users{" +
                "profilepic='" + profilepic + '\'' +
                ", userName='" + userName + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", userId='" + userId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", status='" + status + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", statusImage='" + statusImage + '\'' +
                ", userBio='" + userBio + '\'' +
                ", userLink='" + userLink + '\'' +
                ", fullName='" + fullName + '\'' +
                ", followers='" + followers + '\'' +
                ", following='" + following + '\'' +
                ", posts='" + posts + '\'' +
                '}';
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users() {
    }    // Signup constructor   using this in firebase in signup activity


    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserLink() {
        return userLink;
    }

    public void setUserLink(String userLink) {
        this.userLink = userLink;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}


