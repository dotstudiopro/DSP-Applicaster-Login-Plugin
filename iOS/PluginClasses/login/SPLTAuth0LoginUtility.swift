//
//  DTSZAuth0iOSLoginUtility.swift
//  DotstudioPRO
//
//  Created by Ketan Sakariya on 22/09/16.
//  Copyright © 2016 ___DotStudioz___. All rights reserved.
//

import Foundation
import Lock
import Auth0
import JWTDecode
import SimpleKeychain

//import FBSDKCoreKit

open class SPLTAuth0LoginUtility {

    public enum InitialLoginScreen: Int {
        case login
        case signup
        case resetPassword
    }
//    public var initialLoginScreen: InitialLoginScreen = .login
    
    static public let shared = SPLTAuth0LoginUtility()
    
    open var sourceViewController: UIViewController?
    public let keychain = A0SimpleKeychain(service: "Dotstudio")
    
    public init() {}

    open func showLoginAndSubscriptionController(viewController: UIViewController, initialLoginScreen: InitialLoginScreen = .login, completion: @escaping (_ bSuccess: Bool) -> Void, completionError: @escaping (_ error: NSError) -> Void) {
        
        if let strClientToken = SPLTLoginPluginUtility.strClientToken {
            // Login is already done. show subscription controller.
            SPLTTokenAPI().refreshAllTokens { (strToken) in
                let storyboardViewController = UIStoryboard(name: "subscribe", bundle: nil)
                let spltSubscribeViewController = storyboardViewController.instantiateViewController(withIdentifier: "SPLTSubscribeViewController")
                viewController.present(spltSubscribeViewController, animated: true) {
                    // completion
                }

            }
            
        } else {
            self.showLoginControllerFrom(viewController: viewController, initialLoginScreen: initialLoginScreen, completion: completion, completionError: completionError)
        }
    }
    
    open func showLoginControllerFrom(viewController: UIViewController, initialLoginScreen: InitialLoginScreen = .login, completion: @escaping (_ bSuccess: Bool) -> Void, completionError: @escaping (_ error: NSError) -> Void) {
        self.sourceViewController = viewController
        SPLTTokenAPI().getToken { (strToken) in
            self.parseAccessTokenAndShowLoginController(strAccessToken: strToken, initialLoginScreen: initialLoginScreen, completion: completion, completionError: completionError)
        }
        //self.showLoginController()
    }
    
    open func parseAccessTokenAndShowLoginController(strAccessToken: String, initialLoginScreen: InitialLoginScreen, completion: @escaping (_ bSuccess: Bool) -> Void, completionError: (_ error: NSError) -> Void) {
        do {
            let jwt = try decode(jwt: strAccessToken)
            print(jwt.body)
//            jwt.body
            if let strCompanyId = jwt.body["iss"] as? String {
                self.showLoginController(strCompanyId: strCompanyId, initialLoginScreen: initialLoginScreen, completion: completion, completionError: completionError)
            } else {
                print("Company ID is not received in Login Access Token")
            }
            
        } catch {
            print("Something went wrong!")
        }

    }
    
    open func showLoginController(strCompanyId: String, initialLoginScreen: InitialLoginScreen, completion: @escaping (_ bSuccess: Bool) -> Void, completionError: (_ error: NSError) -> Void) {
        // c: 5690134e97f8154731aeed2d
        if let sourceViewController = self.sourceViewController {
            
            
            
            Lock
                .classic(clientId: SPLTLoginPluginUtility.auth0ClientId, domain: SPLTLoginPluginUtility.auth0Domain)
                // withConnections, withOptions, withStyle, etc
                .withOptions {
                    $0.closable = true
                    $0.parameters = ["c":strCompanyId]
//                    $0.usernameStyle = [.Username]
//                    $0.allow = [.Login, .ResetPassword]
                    switch initialLoginScreen {
                        case .login: $0.initialScreen = .login
                        case .signup: $0.initialScreen = .signup
                        case .resetPassword: $0.initialScreen = .resetPassword
                    }
                }
//                .withOptions {
//                    $0.oidcConformant = true
//                    $0.scope = "openid profile"
//                }
                .withStyle {
                    // style.
                    $0.logo = LazyImage(name: "LoginIcon")
                    $0.title = ""
                    $0.backgroundColor = SPLTLoginPluginUtility.backgroundColor
                    $0.headerColor = SPLTLoginPluginUtility.headerColor
                    $0.titleColor = SPLTLoginPluginUtility.titleColor
////                        $0.inputTextColor = UIColor.white
////                    $0.primaryColor = UIColor.white
//                    $0.secondaryButtonColor = UIColor.black
//                    $0.buttonTintColor = UIColor.white
//
//                    $0.tabTextColor = UIColor.black
//                    $0.tabTintColor = UIColor(hex6: 0xF00886) //UIColor.black
//
//                    $0.primaryColor = UIColor.black //(hex6: 0xED1D1D)

                }
                .onAuth { credentials in
                    // Let's save our credentials.accessToken value
                    print("Authentication is successful")
                    print(credentials)
                    if let accessToken = credentials.accessToken {
                        SPLTAuth0SessionManager.shared.storeTokens(accessToken, refreshToken: credentials.refreshToken)
                    }
                    SPLTAuth0SessionManager.shared.retrieveProfile { error in
                        DispatchQueue.main.async {
                            if let strClientToken = SPLTAuth0SessionManager.shared.profile?.customClaims?["spotlight"] as? String {
                                self.keychain.setString(strClientToken, forKey: "clientToken")
                                SPLTLoginPluginUtility.strClientToken = strClientToken
//                                    SPLTRouter.strClientToken = strClientToken
//                                    UserDefaults.standard.setValue(strClientToken, forKey: "strClientToken")
//                                    UserDefaults.standard.synchronize()
                                    // Login successful
//                                    self.sendLoginNotificationWithUserData()
                                    completion(true)
                                //print(userInfoProfile)
                            } else {
                                self.showMissingClientTokenAlert()
                                completion(false)
                            }
                        }
                    }
                }
                .onError(callback: { (error) in
                    // error
                    DispatchQueue.main.async {
                        self.showMissingClientTokenAlert()
                        completion(false)
                    }
                })
                .onCancel(callback: {
                    // cancelled.
                    DispatchQueue.main.async {
                        completion(false)
                    }
                })
                .present(from: sourceViewController)
        }
    }
    
    open func showMissingProfileOrTokenAlert() {
        let alert = UIAlertController(title: "Error", message: "Could not retrieve profile or token", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        if let sourceViewController = self.sourceViewController {
            sourceViewController.present(alert, animated: true, completion: nil)
        }
    }
    open func showMissingClientTokenAlert() {
        let alert = UIAlertController(title: "Error", message: "Could not retrieve client token", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        if let sourceViewController = self.sourceViewController {
            sourceViewController.present(alert, animated: true, completion: nil)
        }
    }
    
    open func logoutUser() {
//        self.sendLogoutNotificationWithUserData()
//        SPLTRouter.strClientToken = nil
//        UserDefaults.standard.removeObject(forKey: "strClientToken")
//        UserDefaults.standard.synchronize()
////        FBSDKAccessToken.setCurrent(nil)
//        // Logout successful
//        NotificationCenter.default.post(name: Notification.Name.SPLT_LOGOUT_COMPLETED, object: nil)

    }
    
//    open func sendLoginNotificationWithUserData() {
//        SPLTUserDetailAPI().getUserDetails({ (userDataDict) -> Void in
//            // User detail is received
//            NotificationCenter.default.post(name: Notification.Name.SPLT_LOGIN, object: userDataDict)
//            print(userDataDict)
//        }) { (error) -> Void in
//            // Error occured
//        }
//    }
//    open func sendLogoutNotificationWithUserData() {
//        SPLTUserDetailAPI().getUserDetails({ (userDataDict) -> Void in
//            // User detail is received
//            NotificationCenter.default.post(name: Notification.Name.SPLT_LOGOUT, object: userDataDict)
//            print(userDataDict)
//        }) { (error) -> Void in
//            // Error occured
//        }
//    }
}




