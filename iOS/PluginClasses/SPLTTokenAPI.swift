//
//  SPLTTokenAPI.swift
//  DotstudioPRO
//
//  Created by ketan on 22/02/16.
//  Copyright © 2016 ___DotStudioz___. All rights reserved.
//

import Foundation
import Alamofire
import Auth0
import SimpleKeychain
import JWTDecode

public protocol SPLTTokenAPIDelegate: NSObjectProtocol {
    func didReceiveToken(_ strToken: String)
    func didFailedTokenAPICall()
}

open class SPLTTokenAPI {
    
    open var delegate: SPLTTokenAPIDelegate?
    public let keychain = A0SimpleKeychain(service: "Dotstudio")
    
    public init() {
        
    }
    
    open func getToken(_ completion: @escaping (_ strAccessToken: String) -> Void) {
        
        // MARK: URLStringConvertible
        // apiKey : dpro : c681a9f6a3b9d51502cc3978298feaccfa9f500b
        self.keychain.setString("c681a9f6a3b9d51502cc3978298feaccfa9f500b", forKey: "apiKey")
        var parameters: [String: String] = [:]
        if let strApiKey = self.keychain.string(forKey: "apiKey") {
            parameters["key"] = strApiKey
        }

        Alamofire.request("https://api.myspotlight.tv/token", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).validate().responseJSON { (response) in
            if (response.result.value != nil) {
                if let infoDict = response.result.value as? [String: AnyObject] {
                    //self.delegate?.didReceiveChannels(infoDict)
                    print(infoDict)
                    if let bSuccess = infoDict["success"] as? Bool {
                        if (bSuccess == true) {
                            if let strAccessToken = infoDict["token"] as? String {
                                self.keychain.setString(strAccessToken, forKey: "accessToken")
//                                SPLTRouter.strAccessToken = strAccessToken
//                                UserDefaults.standard.setValue(strAccessToken, forKey: "strAccessToken")
//                                UserDefaults.standard.synchronize()
                                completion(strAccessToken) //"str-Token")
                                return
                            }
                        }
                    }
                }
            } else {
//                self.delegate?.didFailedChannelsAPICall()
            }
        }
        
    }
    
    open func refreshAllTokens(_ completion: @escaping (_ strToken: String?) -> Void) {
        self.getToken { (strAccessToken) -> Void in
            self.refreshToken( { (strClientToken) -> Void in
                completion(strClientToken)
            })
        }
    }
    open func refreshToken(_ completion: @escaping (_ strToken: String?) -> Void) {
        // MARK: URLStringConvertible
        
//        if SPLTRouter.strClientToken == nil {
//            // Logout & return
//            completion(nil)
//            return
//        }
        var headers: [String: String] = [:]
        if let strAccessToken = self.keychain.string(forKey: "accessToken") {
            headers["x-access-token"] = strAccessToken
        }
        if let strAccessToken = self.keychain.string(forKey: "clientToken") {
            headers["x-client-token"] = strAccessToken
        }
        
        
        Alamofire.request("https://api.myspotlight.tv/users/token/refresh", method: .post, parameters: nil, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { (response) in
            if (response.result.value != nil) {
                if let infoDict = response.result.value as? [String: AnyObject] {
                    if let bSuccess = infoDict["success"] as? Bool {
                        if (bSuccess == true) {
                            if let strClientToken = infoDict["client_token"] as? String {
                                self.keychain.setString(strClientToken, forKey: "clientToken")
//                                SPLTRouter.strClientToken = strClientToken
//                                UserDefaults.standard.setValue(strClientToken, forKey: "strClientToken")
//                                UserDefaults.standard.synchronize()
                                completion(strClientToken)
                                return
                            }
                        }
                    }
                }
            } else {
                //                self.delegate?.didFailedChannelsAPICall()
            }
            completion(nil)
        }
        
    }
    
    
    open func checkTokenExpiryAndRefresh(_ completion: @escaping (_ bRefresedToken: Bool) -> Void) {
        if self.isTokenExpired() == true {
            // refresh token & return true
            SPLTTokenAPI().refreshAllTokens({ (strToken) in
                completion(true)
            })
            return
        }
        completion(false)
    }
    open func checkAccessTokenExpiryAndRefresh(_ completion: @escaping (_ bRefresedToken: Bool) -> Void) {
        if self.isAccessTokenExpired() == true {
            // refresh token & return true
            SPLTTokenAPI().refreshAllTokens({ (strToken) in
                completion(true)
            })
            return
        }
        completion(false)
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
        if let strAccessToken = self.keychain.string(forKey: "accessToken") {
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
        if let strClientToken = self.keychain.string(forKey: "clientToken") {
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


