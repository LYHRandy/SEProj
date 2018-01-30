package se.entity;

import java.util.Objects;


/**
 * User Object to be used for CRUD
 */
public class User {
    
    private String email;
    private String password;
    private String macAddress;
    private String name;
    private String gender;
    private String year;
    private String school;
    private String username;
    
    /**
     * User entity constructor
     * 
     * @param macAddress macAddress of specific student
     * @param name  name of specific student
     * @param password pass used by student to login
     * @param email email allocated to student
     * @param gender gender of specific student
     * @param year year student matriculated
     * @param school faculty student studies in
     * @param username username student uses to login
     */
    public User(String macAddress, String name, String password, String email, String gender, String year, String school,String username){
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.year = year;
        this.school = school;
        this.username = username;
    }
    
    /**
     * returns email
     * @return Student's email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * returns password
     * @return Student's password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * returns macaddress
     * @return Student's device macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }
    
    /**
     * returns name
     * @return student's name
     */
    public String getName() {
        return name;
    }

    /**
     * returns gender
     * @return student's gender
     */
    public String getGender() {
        return gender;
    }
    
    /**
     * returns year
     * @return year student matriculated in
     */
    public String getYear(){
        return year;
    }
    
    /**
     * returns school
     * @return faculty student studies in
     */
    public String getSchool(){
        return school;
    }
    
    /**
     * returns username
     * @return username used by student to login
     */
    public String getUsername(){
        return username;
    }

}
