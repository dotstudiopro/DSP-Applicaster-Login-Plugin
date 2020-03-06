//
//  ViewController
//  DSP-Applicaster-Login-Plugin
//
//  Created by Ketan Sakariya on 14/02/20.
//  Copyright © 2020 Ketan Sakariya. All rights reserved.
//

import UIKit
//import Dotstudio
import ZappPlugins

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.startLoginPlugin()
    }

    @IBAction func createPluginButtonClicked(_ sender: Any) {
//        let dict = Dictionary(dictionaryLiteral: ("foo", "bar"))
//        if let model = ZPPluginManager.pluginModels()?.first {
//            let pluginAdapter = FullScreenPlugin(pluginModel:model,
//                                                 screenModel: ZLScreenModel(object: dict),
//                                                 dataSourceModel: nil)
//            if let vc = pluginAdapter?.createScreen() {
//                self.navigationController?.pushViewController(vc, animated: true)
//            }
//        }
        self.startLoginPlugin()
    }
    
    func startLoginPlugin() {
//        if let loginPlugin = ZAAppConnector.sharedInstance().pluginsDelegate?.loginPluginsManager?.createWithUserData(),
//            let extensions = [:] as? [String : NSObject] {
//            if let isUserComply = loginPlugin.isUserComply?(policies: extensions),
//                isUserComply == true {
//                //show welcome screen
//                let alert = UIAlertController(title: "Welcome", message: "You are logged in now.", preferredStyle: .alert)
//                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
//                self.present(alert, animated: true)
//            } else {
//                //show login screen
////                guard let facebookInfoVC = segue.destination as? FacebookInfoViewController else { return }
////                facebookInfoVC.loginPlugin = loginPlugin
//            }
//        }
        
        SPLTLoginPluginConstants.apiKey = "c681a9f6a3b9d51502cc3978298feaccfa9f500b"
        SPLTLoginPluginConstants.auth0ClientId = "fRI7uheX6IzdEKa4GXpQAAWBsIGX67oR"
        let loginPlugin = LoginPlugin()
        loginPlugin.executeOnApplicationReady(displayViewController: self) {
            print("executed on app ready")
        }
    }

}

