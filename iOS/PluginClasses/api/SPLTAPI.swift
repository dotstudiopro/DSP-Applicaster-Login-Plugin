//
//  SPLTAPI.swift
//  DotstudioPRO
//
//  Created by ketan on 17/02/16.
//  Copyright © 2016 ___DotStudioz___. All rights reserved.
//

import Foundation
import Alamofire
import UIKit

public enum SPLTRouter: URLConvertible {
    
//    static let BUNDLE_IDENTIFIER = SPLTConfig.infoDictionaryBundleMain!["CFBundleIdentifier"] as! String
    static var BASE_URL = "https://api.myspotlight.tv" //"http://dev.api.myspotlight.tv"
//    static let COUNTRY_CODE: String = (Locale.current as NSLocale).object(forKey: NSLocale.Key.countryCode) as! String
    
//    static let baseUniversalURLString = "https://api.dotstudiopro.com/v2/\(universalKey)/json/videos"

//    public static var API_KEY = ""

//   public static var strAccessToken: String? {
//        didSet {
//            if let strAccessToken = SPLTRouter.strAccessToken {
//
//                do {
//                    let jwt = try decode(strAccessToken)
//                    if let strCompanyId = jwt.body["iss"] as? String {
//                        self.str_company_id = strCompanyId
//                    }
//                    if let contextObject = jwt.body["context"] as? [String: Any] {
//                        if let strCompanyName = contextObject["name"] as? String {
//                            self.str_company_name = strCompanyName
//                        }
//                    }
//                } catch {
//                    print("Something went wrong while retrieving company id from access token!")
//                }
//
//                SPLTBaseAPI.spltTokenHandler = SPLTTokenHandler(
//                    API_KEY: SPLTRouter.API_KEY, //"12345678",
//                    baseURLString: SPLTRouter.BASE_URL, //baseURLString,
//                    accessToken: strAccessToken, //"abcd1234",
//                    clientToken: SPLTRouter.strClientToken //"ef56789a"
//                )
//            }
//        }
//    }
//    public static var strClientToken: String? {
//        didSet {
//            SPLTUser.sharedInstance.updateUserId()
////            if (strClientToken == nil) {
////                SPLTUser.sharedInstance.userId = nil
////            }
//            if let strAccessToken = SPLTRouter.strAccessToken {
//                SPLTBaseAPI.spltTokenHandler = SPLTTokenHandler(
//                    API_KEY: SPLTRouter.API_KEY, //"12345678",
//                    baseURLString: SPLTRouter.BASE_URL, //baseURLString,
//                    accessToken: strAccessToken, //"abcd1234",
//                    clientToken: SPLTRouter.strClientToken //"ef56789a"
//                )
//            }
//        }
//    }
    
//    static public var str_company_id: String?
//    static public var str_company_name: String?
    
    case root
    case token
    case refreshToken

    case subscriptionAppleReceiptPost
    case checkSubscriptionStatus(String)
    case getSubscriptionPlans
    case getActiveSubscriptions
    
    // MARK: URLStringConvertible
    var URLString: String {
        let path: String = {
            switch self {
            case .root:
                return "/"
            case .token:
                return SPLTRouter.BASE_URL + "/token"
            case .refreshToken:
                return SPLTRouter.BASE_URL + "/users/token/refresh"
                
            case .subscriptionAppleReceiptPost:
                return SPLTRouter.BASE_URL + "/subscriptions/apple/customer/parse"
            case .checkSubscriptionStatus(let channelId):
                return SPLTRouter.BASE_URL + "/subscriptions/check/\(channelId)"
            case .getSubscriptionPlans:
                return SPLTRouter.BASE_URL + "/subscriptions/summary"
            case .getActiveSubscriptions:
                return SPLTRouter.BASE_URL + "/subscriptions/users/active_subscriptions"

            }
        }()
        return path
    }
    
    public func asURL() throws -> URL {
        let result: (path: String, parameters: Parameters) = {
            switch self {
                case .root:
                    return ("/", [:]) //, ["q": query, "offset": SPLTRouter.perPage * page])
                case .token:
                    return ("/token", [:]) //, ["q": query, "offset": SPLTRouter.perPage * page])
                case .refreshToken:
                    return ("/users/token/refresh", [:])

                case .subscriptionAppleReceiptPost:
                    return ("/subscriptions/apple/customer/parse", [:])
                case .checkSubscriptionStatus(let channelId):
                    return ("/subscriptions/check/\(channelId)", [:])
                case .getSubscriptionPlans:
                    return ("/subscriptions/summary", [:])
                case .getActiveSubscriptions:
                    return ("/subscriptions/users/active_subscriptions", [:])
                default:
                    return ("", [:])
            }
        }()

        let strBaseUrl = SPLTRouter.BASE_URL
        let strUrl = strBaseUrl.appending(result.path)
        let strFinalUrl = strUrl.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        let url = try strFinalUrl.asURL()
        return url
        
//        return searchSuggesionUrl.addingPercentEscapes(using: String.Encoding.utf8)!

//        let url = try SPLTRouter.BASE_URL.asURL()
//        let finalUrl = url.appendingPathComponent(result.path)
//        return try finalUrl
    }


}


open class SPLTAPI {
    
    // With Block methods
//    open func regenerateAccessTokenAndGetJSONResponse(_ strUrl: String, completion: @escaping (_ dictionary: [String: AnyObject]) -> Void, completionError: @escaping (_ error: NSError) -> Void) {
//        SPLTTokenAPI().getToken { (strToken) -> Void in
//            self.getJSONResponse(strUrl, completion: completion, completionError: completionError)
//        }
//    }
    
//    open func getJSONResponse(_ strUrl: String, completion: @escaping (_ dictionary: [String: AnyObject]) -> Void, completionError: @escaping (_ error: NSError) -> Void) {
//
//        if (SPLTRouter.strAccessToken == nil) {
//            self.regenerateAccessTokenAndGetJSONResponse(strUrl, completion: completion, completionError: completionError)
//            return
//        }
//
//        //let strUrl: URLConvertible = SPLTRouter.categories.URLString
//        let headers = [
//            "x-access-token": SPLTRouter.strAccessToken!,
//            ]
//
//            SPLTBaseAPI.sessionManager.request(SPLTRouter.categories, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: headers).validate().responseJSON { (response) in
//                        if let httpURLResponse = response.response {
//                            if (httpURLResponse.statusCode == 403) {
//                                // regenerate accesstoken
//                                self.regenerateAccessTokenAndGetJSONResponse(strUrl, completion: completion, completionError: completionError)
//                                return
//                            }
//                        }
//                        if (response.result.value != nil) {
//                            if let infoDict = response.result.value as? [String: AnyObject] {
//                                completion(infoDict)
//                            }
//                        }
//                        completionError(NSError(domain: "SPLTAPI", code: 1, userInfo: nil))
//            }
//    }
}






