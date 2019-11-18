package com.example.bikeservice;

public class Apiurl {

    private static final String ROOT_URL = "http://192.168.43.155/bikeservice/api/Api.php?apicall=";
    //private static final String ROOT_URL = "http://w3webcreators.com/bikeservice/api/Api.php?apicall=";

    public static final String URL_REGISTRATION = ROOT_URL + "mech_register";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_INDIVIDUALLIST = ROOT_URL + "IndividualList";
    public static final String URL_CORPORATELIST = ROOT_URL + "CorporateList";
    public static final String URL_INDIVIDUALREQ = ROOT_URL + "IndividualReq";
    public static final String URL_REQMECHANIC = ROOT_URL + "service_request";
    public static final String URL_PROFILE = ROOT_URL + "profile";
    public static final String URL_UPDATEPROFILE = ROOT_URL + "UpdateProfile";
    public static final String URL_UPDATEINFO = ROOT_URL + "UpdateInfo";
    public static final String URL_INDIVIDUALRESPONSE = ROOT_URL + "IndividualReq_Res";
    public static final String URL_MECHDETAIL = ROOT_URL + "mech_details";
    public static final String URL_SENDREQUEST = ROOT_URL + "sendRequestToMech";
    public static final String URL_MECHRESPONSE = ROOT_URL + "approve_admdeatil";
    public static final String URL_APPROVEDETAILS = ROOT_URL + "individual_response";
    public static final String URL_USERREGISTRATION = ROOT_URL + "User_Register";
    public static final String URL_MECHLIST = ROOT_URL + "Mech_List";
    public static final String URL_APPROVEMECH = ROOT_URL + "mech_approve";
    public static final String URL_ACTIVEORDERS = ROOT_URL + "ActiveOrders";
    public static final String URL_CURRENTORDERS = ROOT_URL + "USRCurrentOrders";
    public static final String URL_USERLOGIN = ROOT_URL + "userlogin";
    public static final String URL_TIMER = ROOT_URL + "addtime";
    public static final String URL_USERREQUEST = ROOT_URL + "userrequest";
    public static final String URL_USERDETAILS = ROOT_URL + "userdetails";
    public static final String URL_ORDERCNF = ROOT_URL + "confirm_order";
    public static final String URL_USRAPPROVEREQ = ROOT_URL + "user_approve_request";
    public static final String URL_MECHPAST = ROOT_URL + "Mech_past_order";
    public static final String URL_USRPAST = ROOT_URL + "usr_past_order";
    public static final String URL_ADMSELMECH = ROOT_URL + "select_mechanic";
    public static final String URL_ADMSREQHISTORY = ROOT_URL + "AdminReqHistory";
    public static final String URL_ADMADDCORPORATE = ROOT_URL + "Admin_AddCorporate";
    public static final String URL_ADMADDBIKE = ROOT_URL + "Admin_AddBike";
    public static final String URL_BIKELIST = ROOT_URL + "getBike";
    public static final String URL_CRPTLIST = ROOT_URL + "getcrpt";
    public static final String URL_CRPTDETAILS = ROOT_URL + "crptdetails";
    public static final String URL_USRPHONENO = ROOT_URL + "usrphone";
    public static final String URL_ADMINTOKEN = ROOT_URL + "addtoken";
    public static final String URL_NEWREQ = ROOT_URL + "Mech_latestReq";
    public static final String URL_USERCODE = ROOT_URL + "useruniquecode";
    public static final String URL_ADMSREQSTATUS = ROOT_URL + "AdminReqStatus";

    public static final String URL_USERPROFILE = ROOT_URL + "Userprofile";
    public static final String URL_USERINFO = ROOT_URL + "UpdateUsrInfo";
    public static final String URL_USERUPDATEPROFILE = ROOT_URL + "UpdateUsrProfile";
    //notification
    public static final String URL_NOTIFICATION = ROOT_URL + "sendUserSinglePush";
    public static final String URL_NOTIFICATIONMECH = ROOT_URL + "sendMechSinglePush";
    public static final String URL_NOTIFYADMIN = ROOT_URL + "sendAdminSinglePush";
    //forgotpass
    public static final String URL_CHANGEPASSUSR = ROOT_URL + "UserPassword";
    public static final String URL_CHANGEPASSMECH = ROOT_URL + "MechPassword";
    //feedback
    public static final String URL_USRFEEDBACK = ROOT_URL + "userfeedback";
    public static final String URL_MECHRATING = ROOT_URL + "mechrating";
    public static final String URL_VIEWFEEDBACK = ROOT_URL + "View_Feedback";

    public static final String URL_ADDSERVICE = ROOT_URL + "addsrvc";
    public static final String URL_FETCHSERVICECOMBO = ROOT_URL + "fetchsrvcombo";
    public static final String URL_FETCHSERVICE = ROOT_URL + "fetchsrvc";

    public static final String URL_FETCHUSERINFO = ROOT_URL + "userinformtion";
    public static final String URL_USRFINDGARAGE= ROOT_URL + "Findlocation";
    public static final String URL_USREMERGENCY= ROOT_URL + "Emergency";
    public static final String URL_MECHEMERGENCY= ROOT_URL + "MechEmergency";
    public static final String URL_MECHEMERGENCYDETAILS= ROOT_URL + "mechemerg_details";
    public static final String URL_MECHEMERGENCYRES= ROOT_URL + "Emergency_Res";
    public static final String URL_USEREMERGENCYRCRD= ROOT_URL + "usershow_emerg";

    public static final String URL_MECHTRACKING= ROOT_URL + "MechEmergencyReq";
    public static final String URL_MECHEMERGACTIVE= ROOT_URL + "emergency_active";
    public static final String URL_UPDATEMECHLOC= ROOT_URL + "Updatelocation";

    public static final String URL_USEREXISTS= ROOT_URL + "exist";
    public static final String URL_MECHEXISTS= ROOT_URL + "Mechexist";

    public static final String URL_MECHREGLOCATION= ROOT_URL + "mech_register_location";

    public static final String URL_MECHCHANGEPWD= ROOT_URL + "ChangePassword";
    public static final String URL_USRCHANGEPWD= ROOT_URL + "UsrChangePassword";

    public static final String URL_EMERGWORK= ROOT_URL + "emergaddtime";
    public static final String URL_EMERGWORKCODE= ROOT_URL + "useremerguniquecode";

    public static final String URL_EMERGRESDATE= ROOT_URL + "datevalue";

}

