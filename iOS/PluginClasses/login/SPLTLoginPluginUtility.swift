//
//  LoginPluginConstants.swift
//  DSP-Applicaster-Login-Plugin
//
//  Created by Ketan Sakariya on 20/02/20.
//  Copyright © 2020 Ketan Sakariya. All rights reserved.
//

import Foundation
import UIKit
import JWTDecode

public class SPLTLoginPluginUtility {
    
    static public var strAccessToken: String? = nil
    static public var strClientToken: String? = nil
    
    static public var show_on_startup: Bool = true
    
    static public var apiKey: String = ""
    static public var auth0ClientId: String = "" //nil //"fRI7uheX6IzdEKa4GXpQAAWBsIGX67oR"
    static public var auth0Domain: String = "dotstudiopro.auth0.com"
    
    static public var backgroundColor: UIColor = .white
    static public var headerColor: UIColor = .white
    static public var titleColor: UIColor = .black
    
    
    public static let shared = SPLTLoginPluginUtility()
    
    init() {
//        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(self.handleDidEnterBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.handleWillEnterForeground), name: UIApplication.willEnterForegroundNotification, object: nil)

    }
    deinit {
         NotificationCenter.default.removeObserver(self)
    }
    
    //MARK: - Handle Notifications
    var isAppInBackground: Bool = false
    @objc open func handleDidEnterBackground() {
//        self.isAppInBackground = true
//        self.last_sent = nil
//        #if os(iOS)
//        SPLTBackgroundTaskUtility.sharedInstance.beginBackgroundUpdateTask()
//        #endif
//        self.myBackgroundQueue.async { () -> Void in
//            self.session_id = nil
//            if let analyticsEventsAPIObject = self.analyticsEventsAPIObjects.first {
//                if analyticsEventsAPIObject.analyticsEvents.count > 0 {
//                    self.flushEvents()
//                } else {
//                    analyticsEventsAPIObject.session_id = nil
//                }
//            }
//        }
    }
    @objc open func handleWillEnterForeground() {
//        #if os(iOS)
//        SPLTBackgroundTaskUtility.sharedInstance.completeBackgroundTask()
//        #endif
//        self.isAppInBackground = false
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
        if let strAccessToken = SPLTLoginPluginUtility.strAccessToken {
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
        if let strClientToken = SPLTLoginPluginUtility.strClientToken {
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
