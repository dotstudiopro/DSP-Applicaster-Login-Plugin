//
//  LoginPlugin.swift
//  DSP-Applicaster-Login-Plugin
//
//  Created by Ketan Sakariya on 14/02/20.
//  Copyright © 2020 Ketan Sakariya. All rights reserved.
//

import Foundation
import ZappPlugins
import SimpleKeychain
import JWTDecode
//import FacebookCore
//import FacebookLogin

/*
 This is an example for a login plugin imlementation.
 This shows how to use the ZPLoginProviderProtocol and the ZPLoginProviderUserDataProtocol
 in order to create and integrate any login plugin that is needed
 in this example we choose to implemented the facebook login plugin
 This is the main class of the implementation
 **/
@objc class LoginPlugin: NSObject, ZPAppLoadingHookProtocol, ZPLoginProviderProtocol, ZPLoginProviderUserDataProtocol {
    
    public var configurationJSON: NSDictionary?
    public let keychain = A0SimpleKeychain(service: "Dotstudio")
    var displayViewController: UIViewController?

    var loginCompletion: ((ZPLoginOperationStatus) -> Void)?
//    var fbToken: AccessToken?
//    var loginManager:LoginManager?
    
    public required override init() {
        super.init()
    }
    
    func parseConfigurationJSON() {
        if let show_on_startup = configurationJSON?["show_on_startup"] as? String {
            LoginPluginConstants.show_on_startup = show_on_startup.boolValue()
        }
        if let apiKey = configurationJSON?["apiKey"] as? String {
            LoginPluginConstants.apiKey = apiKey
        }
        if let auth0ClientId = configurationJSON?["auth0ClientId"] as? String {
            LoginPluginConstants.auth0ClientId = auth0ClientId
        }

        if let strBackgroundColor = configurationJSON?["backgroundColor"] as? String,
            let backgroundColor = UIColor(argbHexString: strBackgroundColor) {
            LoginPluginConstants.backgroundColor = backgroundColor
        }
        if let strHeaderColor = configurationJSON?["headerColor"] as? String,
            let headerColor = UIColor(argbHexString: strHeaderColor) {
            LoginPluginConstants.headerColor = headerColor
        }
        if let strTitleColor = configurationJSON?["titleColor"] as? String,
            let titleColor = UIColor(argbHexString: strTitleColor) {
            LoginPluginConstants.titleColor = titleColor
        }
    }
    
//MARK: ZPAppLoadingHookProtocol
    /*
      This method called after Plugins loaded locally, but the account load failed
      */
    @objc func executeOnFailedLoading(completion: (() -> Void)?) {
        
    }
    
    /*
        This method called after Plugins loaded, and also after initial account data retrieved, you can add logic that not related to the application data.
    */
    @objc func executeOnLaunch(completion: (() -> Void)?) {
        
    }

    /*
        This method called after all the data loaded and before viewController presented.
    */
    @objc func executeOnApplicationReady(displayViewController: UIViewController?, completion: (() -> Void)?) {
        self.displayViewController = displayViewController
        if LoginPluginConstants.show_on_startup {
            SPLTAuth0LoginUtility.shared.showLoginControllerFrom(viewController: displayViewController!, completion: { (bSuccess) in
                print("success")
            }) { (error) in
                print("error")
                // handle error.
            }
        }
    }

    /*
      This method called after viewController is presented.
      */
    @objc func executeAfterAppRootPresentation(displayViewController: UIViewController?, completion: (() -> Void)?) {
//        let alert = UIAlertController(title: "ZPAppLoadingHookProtocol", message: "executeAfterAppRootPresentation", preferredStyle: .alert)
//        alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
//        displayViewController?.present(alert, animated: true)
    }

    /*
      This method called when the application:continueUserActivity:restorationHandler is called.
      */
    @objc func executeOnContinuingUserActivity(_ userActivity: NSUserActivity?, completion: (() -> Void)?) {
        
    }

    
//MARK: ZPLoginProviderUserDataProtocol
    
    /**
     This methood is called in order to verify if we need to start a login flow with respect to the policies dictionary
     @params policies - dictionary containing policies to be considered when returning the result, if passed as nil - same as isAuthenticated() method.
     Returns bool value indicating if the user is already verified if not  we start the login proccess
     in this example we check if the login token exists and if it is valid
     */
    public func isUserComply(policies:[String: NSObject]) -> Bool {
        return self.isAuthenticated()
    }
    
    /**
     Getter to the user Token usually can be used for authentication check
    */
    public func getUserToken() -> String {
        if let strClientToken = LoginPluginConstants.strClientToken {
            return strClientToken
        }
        return ""
    }
    
    /**
     Setter for the user Token - usually set after login success
     @Params: the authentication recieved when login successfuly
     */
    public func setUserToken(token: String?) {
        if let strClientToken = token {
            LoginPluginConstants.strClientToken = strClientToken
        }
    }
    
//MARK: ZPLoginProviderProtocol
    
    /**
     Initialization of login Plugin instance
     @Params: configurationJSON - dictionary containing the Plugin setitngs as defined in the plugin manifest
     */
    public required init(configurationJSON: NSDictionary?) {
        super.init()
//        self.loginManager = LoginManager()
        self.configurationJSON = configurationJSON
        self.parseConfigurationJSON()
    }
    
    /**
     Test User in order to login successfuly use these credentials
     user name: jorge_mtglsje_valdano@tfbnw.net
     password: applicaster is the best
    **/
    
    /**
     This method is being called after the isAuthenticated() method returned a false value, meaning, the user is not logged in.
     It starts the login process.
     The completion should always be called when the process is done - no matter what is the result.
     */
    public func login(_ additionalParameters: [String : Any]?, completion: @escaping ((ZPLoginOperationStatus) -> Void)) {
        print("start login.")
        if let displayViewController = self.displayViewController {
            SPLTAuth0LoginUtility.shared.showLoginControllerFrom(viewController: displayViewController, completion: { (bSuccess) in
            print("success")
                if bSuccess {
                    completion(.completedSuccessfully)
                } else {
                    completion(.failed)
                }
            }) { (error) in
                print("error")
                completion(.failed)
            }
        } else {
            completion(.failed)
        }
    }
    
    /**
     This method is being called in order to start logout process.
     The completion should always be called when the process is done - no matter what is the result.
    */
    public func logout(_ completion: @escaping ((ZPLoginOperationStatus) -> Void)) {
        SPLTAuth0LoginUtility.shared.logoutUser()
        completion(.completedSuccessfully)
    }
    
    /**
     This methood is called in order to verify if we need to start a login flow
     for example play method is invoked on a player, the player first checks if a login plugin exist
     and if so it creates an instance of this plugin, and invokes this method to check if the user is already logged in
     Returns bool value indicating if the user is already verified if not we start the login proccess
    */
    public func isAuthenticated() -> Bool {
        return self.isTokenValid()
    }
    
    /**
     This methood is called in order to verify if authorization flow is in process
    */
    public func isPerformingAuthorizationFlow() -> Bool {
        return true
    }

//MARK: Public
//    public func getUserExpirationDate() -> Date? {
//        return UserDefaults.standard.object(forKey: "dsp_expiration_date") as? Date
//    }
//
//    public func setUserExpirationDate(token: Date?) {
//        UserDefaults.standard.set(token, forKey:"dsp_expiration_date")
//    }
    
    func deleteCookies() {
        let storage = HTTPCookieStorage.shared
        for cookie in storage.cookies! {
            storage.deleteCookie(cookie)
        }
    }
    
//MARK: Private
    private func isTokenValid() -> Bool {
        return !self.isTokenExpired()
        // check JWT token & validate for expiry
//        var retVal = false
//        if let expirationDate = self.getUserExpirationDate(),
//            self.getUserToken().isEmpty == false {
//            let currentDate = Date()
//            if expirationDate > currentDate || expirationDate == currentDate {
//                retVal = true
//            }
//        }
//        return retVal
    }
    
    open func isTokenExpired() -> Bool {
        if self.isAccessTokenExpired() {
            return true
        }
        if self.isClientTokenExpired() {
            return true
        }
        return false
    }
    open func isAccessTokenExpired() -> Bool {
        if let strAccessToken = LoginPluginConstants.strAccessToken {
            do {
                let jwt = try decode(jwt: strAccessToken)
                if let iExpiryMiliseconds = jwt.body["expires"] as? Int {
                    let iCurMiliseconds = Int(Date().timeIntervalSince1970 * 1000)
                    print(iExpiryMiliseconds)
                    print(iCurMiliseconds)
                    if iExpiryMiliseconds < iCurMiliseconds {
                        print("Access Token Expired")
                        return true
                    } else {
                        print("Access Token not Expired")
                        return false
                    }
                }
            } catch {
                print("Something went wrong!")
            }
        }
        return true // return true as access token is not available.
    }
    open func isClientTokenExpired() -> Bool {
        if let strClientToken = LoginPluginConstants.strClientToken {
            do {
                let jwt = try decode(jwt: strClientToken)
                if let iExpiryMiliseconds = jwt.body["expires"] as? Int {
                    let iCurMiliseconds = Int(Date().timeIntervalSince1970 * 1000)
                    print(iExpiryMiliseconds)
                    print(iCurMiliseconds)
                    if iExpiryMiliseconds < iCurMiliseconds {
                        print("Client Token Expired")
                        return true
                    } else {
                        print("Client Token not Expired")
                    }
                }
                
            } catch {
                print("Something went wrong!")
            }
        }
        return false
    }
}


