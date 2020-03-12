//
//  DSUtility.swift
//  RevryApp
//
//  Created by Ketan Sakariya on 07/06/18.
//  Copyright © 2018 Dotstudioz. All rights reserved.
//

import Foundation
import UIKit

open class DSUtility {
    public static let shared = DSUtility()
    
    open func showAlert(_ message: String?, onViewController viewController: UIViewController) -> UIAlertController {
        return self.showAlert(nil, message: message, preferredStyle: .alert, onViewController: viewController)
    }
    open func showAlert(_ title: String?, message: String?, preferredStyle: UIAlertController.Style, onViewController viewController: UIViewController) -> UIAlertController {
        let okAlertAction = UIAlertAction(title: "Ok", style: .default, handler: nil)
        return self.showAlert(title, message: message, preferredStyle: preferredStyle, onViewController: viewController, alertActions: [okAlertAction])
    }
    open func showAlert(_ title: String?, message: String?, preferredStyle: UIAlertController.Style, onViewController viewController: UIViewController, alertActions: [UIAlertAction]) -> UIAlertController {
        
        let alertController = UIAlertController(title: title, message: message, preferredStyle: preferredStyle)
        for alertAction in alertActions {
            alertController.addAction(alertAction)
        }
        viewController.present(alertController, animated: true)
        return alertController
    }
    open func showAlertOnWindow(_ title: String?, message: String?, preferredStyle: UIAlertController.Style) -> UIAlertController {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: preferredStyle)
        alertController.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
//        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
//            if let window = appDelegate.window {
//                window.rootViewController?.present(alertController, animated: true)
//            }
//        }
        return alertController
    }
    
    open func showAllFonts() {
        for family: String in UIFont.familyNames
        {
            print("\(family)")
            for names: String in UIFont.fontNames(forFamilyName: family)
            {
                print("== \(names)")
            }
        }
    }
    
}






