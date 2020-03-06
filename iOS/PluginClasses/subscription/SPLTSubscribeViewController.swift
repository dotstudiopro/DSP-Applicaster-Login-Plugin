//
//  SPLTSubscribeViewController.swift
//  SamapleProject
//
//  Created by Anwer on 5/20/18.
//  Copyright © 2018 Anwer. All rights reserved.
//

import UIKit
//import DotstudioPRO
import SwiftyStoreKit
import StoreKit
import Alamofire

//enum SPLTRegisteredPurchase: String {
//    case monthly = "monthly"
//    case yearly = "yearly"
//}
//
//open class SPLTSubscriptionConfig {
//    static public let subscriptionsMetadata: [String: Any] = [
//        SPLTRegisteredPurchase.monthly.rawValue : ["price": 6.99],
//        SPLTRegisteredPurchase.yearly.rawValue : ["price": 59.99]
//    ]
//}

public protocol SPLTSubscribeViewControllerDelegate {
    func didSubscribeSuccessfully(_ dsSubscribeViewController: SPLTSubscribeViewController)
    func didFailedToSubscribe(_ dsSubscribeViewController: SPLTSubscribeViewController)
    func didSkipToSubscribe(_ dsSubscribeViewController: SPLTSubscribeViewController)
}
open class SPLTSubscribeViewController: UIViewController {
    
    let strPrivacyUrl = "https://revry.tv/privacy-policy"
    let strTermsUrl = "https://revry.tv/terms-of-service"
    let strContactusUrl = "https://revry.tv/contact-us"
    
    #if os(iOS)
    override open var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        get {
            return UIInterfaceOrientationMask.portrait
        }
    }
    
    @IBOutlet weak var buttonClose: UIButton?
    
    override open var preferredStatusBarStyle: UIStatusBarStyle {
        return .default
    }
    
    @IBOutlet weak var tableView: UITableView?
    #elseif os(tvOS)
    @IBOutlet weak var labelPaymentDescriptionLink: UILabel?
    @IBOutlet weak var labelRevrySupportLinks: UILabel?
    @IBOutlet weak var labelAdFreeDescription: UILabel?
    @IBOutlet weak var labelSelectYourPlan: UILabel?
    @IBOutlet weak var collectionView: UICollectionView?
    #endif
    
    var delegate: SPLTSubscribeViewControllerDelegate?
     let appBundleId =  Bundle.main.bundleIdentifier //"com.dotstudioz.dotstudioprotest"
    var autoRenewableIsAtomic: Bool = true
    var strProductId: String?
    
    var autoRenewableSubscriptionProduct: SPLTSubscriptionProduct?
    
    
    
    

    override open func viewDidLoad() {
        super.viewDidLoad()
        #if os(iOS)
        self.tableView?.register(UINib(nibName: "SPLTSubscriptionCell", bundle: nil), forCellReuseIdentifier: "SPLTSubscriptionCellIdentifier")
        self.tableView?.tableFooterView = UIView()
        self.tableView?.estimatedRowHeight = 160.0

//            let imageSizeCloseIcon = CGSize(width: 66, height: 66)
//            let imageCloseButton = UIImage(icon: .FAClose, size: imageSizeCloseIcon, textColor: .black, backgroundColor: .clear)
//            self.buttonClose?.setImage(imageCloseButton, for: .normal)
        #elseif os(tvOS)
        self.labelSelectYourPlan?.textColor = .black
        self.labelAdFreeDescription?.textColor = .black
        self.labelRevrySupportLinks?.textColor = .black
        self.labelPaymentDescriptionLink?.textColor = .black
        self.collectionView?.register(UINib(nibName: "RevrySubscriptionCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "RevrySubscriptionCollectionViewCell")
        self.collectionView?.register(UINib(nibName: "RevrySubscriptionWatchFreeCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "RevrySubscriptionWatchFreeCollectionViewCell")
            self.resetCollectionViewSize(CGSize(width: 430, height: 430))
        #endif

        // Do any additional setup after loading the view.
        let receiptData = SwiftyStoreKit.localReceiptData
        if let receiptString = receiptData?.base64EncodedString(options: []) {
            print(receiptString)
        }
        
//        self.showProgress()
        //SPLTInApp
        SPLTSubscriptionUtility.shared.loadSubscriptionProducts(completion: { (subscriptionProductListArray) in
             #if os(iOS)
                self.tableView?.reloadData()
            #elseif os(tvOS)
                self.collectionView?.reloadData()
            #endif
//            self.hideProgress()
        }) { (error) in
            print(error)
//            self.hideProgress()
        }
    }
    
    
    //MARK: -
    //MARK: - IBAction methods

    @IBAction func didClickCloseButton(sender: UIButton) {
        print("Close Button Clicked")
        self.delegate?.didSkipToSubscribe(self)
    }
    
    @IBAction func didClickWatchForFreeButton(sender: UIButton) {
        print("Whatch For Free Button Clicked")
        self.delegate?.didSkipToSubscribe(self)
    }
    
    @IBAction func didClickPrivacyButton(_ sender: Any) {
        self.openExternalUrl(self.strPrivacyUrl)
    }
    
    @IBAction func didClickTermsButton(_ sender: Any) {
        self.openExternalUrl(self.strTermsUrl)
    }
    
    @IBAction func didClickContactUsButton(_ sender: Any) {
        self.openExternalUrl(self.strContactusUrl)
    }
    
    func openExternalUrl(_ strUrl: String) {
        #if os(iOS)
            guard let url = URL(string: strUrl) else {
                return //be safe
            }
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
                UIApplication.shared.openURL(url)
            }
        #endif
    }
    
    //MARK: -
    //MARK: - subscription methods
    func purchaseSubscriptionWithProduct(subscriptionProduct:SPLTSubscriptionProduct) {
        self.autoRenewableSubscriptionProduct = subscriptionProduct
        self.purchase(subscriptionProduct, atomically: self.autoRenewableIsAtomic)
    }
    
    /*
    func autoRenewableGetInfo() {
        if let autoRenewableSubscription = self.autoRenewableSubscription {
            getInfo(autoRenewableSubscription)
        }
    }
     
    func getInfo(_ purchase: RevryRegisteredPurchase) {
        NetworkActivityIndicatorManager.networkOperationStarted()
        SwiftyStoreKit.retrieveProductsInfo(["monthly","yearly"]) { result in
            NetworkActivityIndicatorManager.networkOperationFinished()
        }
    }*/
    
    func purchase(_ subscriptionProduct:SPLTSubscriptionProduct, atomically: Bool) {
//        self.showProgress()
        if let strAppleProductId = subscriptionProduct.strAppleProductId {
//            NetworkActivityIndicatorManager.networkOperationStarted()
            SwiftyStoreKit.purchaseProduct(strAppleProductId, atomically: atomically) { result in
//                NetworkActivityIndicatorManager.networkOperationFinished()
//                self.hideProgress()
                if case .success(let purchase) = result {
                    let downloads = purchase.transaction.downloads
                    if !downloads.isEmpty {
                        SwiftyStoreKit.start(downloads)
                    }
                    // Deliver content from server, then:
                    if purchase.needsFinishTransaction {
                        SwiftyStoreKit.finishTransaction(purchase.transaction)
                    }
                    self.fetchAndSendReceipt()
                } else {
                    if let alert = self.alertForPurchaseResult(result) {
                        self.showAlert(alert)
                    }
                }
            }
                
        }
    }
    
    func fetchAndSendReceipt() {
//        self.showProgress()

        let receiptData = SwiftyStoreKit.localReceiptData
        if let receiptString = receiptData?.base64EncodedString(options: []) {
            self.sendReceipt(receiptString)
            return
        }
        
        SwiftyStoreKit.fetchReceipt(forceRefresh: false) { result in
            switch result {
            case .success(let receiptData):
//                self.hideProgress()
                let encryptedReceipt = receiptData.base64EncodedString(options: [])
                self.sendReceipt(encryptedReceipt)
                print("Fetch receipt success:\n\(encryptedReceipt)")
            case .error(let error):
//                self.hideProgress()
                print("Fetch receipt failed: \(error)")
            }
        }
    }
    func sendReceipt(_ encryptedReceipt: String) {
        if let autoRenewableSubscriptionProduct = self.autoRenewableSubscriptionProduct {
            if let strProductId = autoRenewableSubscriptionProduct.strAppleProductId {
//                self.showProgress()
                SPLTSubscriptionAPI().postSubscriptionReceiptData(encryptedReceipt, strProductId: strProductId, completion: { (infoDict) in
                    print(infoDict)
//                    self.hideProgress()
//                    self.trackSubscriptionEvent(autoRenewableSubscriptionProduct)
                    self.showSuccessMessageToUser()
                }) { (error) in
                    print(error.localizedDescription)
//                    self.hideProgress()
                    if let strErrorMessage = error.userInfo["error"] as? String {
                        _ = DSUtility.shared.showAlert("Error", message: strErrorMessage, preferredStyle: .alert, onViewController: self)
                    } else {
                        _ = DSUtility.shared.showAlert("Error", message: "There has been an error while subscribing. Please try again after sometime.", preferredStyle: .alert, onViewController: self)
                    }
                }
            }
        }
    }
    
    func showSuccessMessageToUser() {
        let okAlertAction = UIAlertAction(title: "Ok", style: .default) { (alertAction) in
            self.delegate?.didSubscribeSuccessfully(self)
        }
        _ = DSUtility.shared.showAlert("Revry", message: "Subscribe successfully.", preferredStyle: .alert, onViewController: self, alertActions: [okAlertAction])
    }
    
    
    @IBAction func restorePurchases() {
//        NetworkActivityIndicatorManager.networkOperationStarted()
        SwiftyStoreKit.restorePurchases(atomically: true) { results in
//            NetworkActivityIndicatorManager.networkOperationFinished()
            for purchase in results.restoredPurchases {
                let downloads = purchase.transaction.downloads
                if !downloads.isEmpty {
                    SwiftyStoreKit.start(downloads)
                } else if purchase.needsFinishTransaction {
                    // Deliver content from server, then:
                    SwiftyStoreKit.finishTransaction(purchase.transaction)
                }
            }
            self.showAlert(self.alertForRestorePurchases(results))
        }
    }
    
    //MARK:
    //Event will be called when any button will be pressed from Apple TV Remote
    //We can specify the type of event and can handle the functions
    open override func pressesBegan(_ presses: Set<UIPress>, with event: UIPressesEvent?) {
        print("Menu Press event called")
        for press in presses{
            //Specify the type of event which is to be handeled by the app
            if(press.type == .menu){
                //Do what you want
                self.delegate?.didSkipToSubscribe(self)
            }else{
                super.pressesEnded(presses, with: event)
            }
        }
    }
}

//MARK: -
//MARK: - User facing alerts
extension SPLTSubscribeViewController {
    
    func alertWithTitle(_ title: String, message: String) -> UIAlertController {
        
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
        return alert
    }
    
    func showAlert(_ alert: UIAlertController) {
        guard self.presentedViewController != nil else {
            self.present(alert, animated: true, completion: nil)
            return
        }
    }
    
    func alertForProductRetrievalInfo(_ result: RetrieveResults) -> UIAlertController {
        
        if let product = result.retrievedProducts.first {
            let priceString = product.localizedPrice!
            return alertWithTitle(product.localizedTitle, message: "\(product.localizedDescription) - \(priceString)")
        } else if let invalidProductId = result.invalidProductIDs.first {
            return alertWithTitle("Could not retrieve product info", message: "Invalid product identifier: \(invalidProductId)")
        } else {
            let errorString = result.error?.localizedDescription ?? "Unknown error. Please contact support"
            return alertWithTitle("Could not retrieve product info", message: errorString)
        }
    }
    
    // swiftlint:disable cyclomatic_complexity
    func alertForPurchaseResult(_ result: PurchaseResult) -> UIAlertController? {
        switch result {
        case .success(let purchase):
            print("Purchase Success: \(purchase.productId)")
            return nil
        case .error(let error):
            print("Purchase Failed: \(error)")
            switch error.code {
            case .unknown: return alertWithTitle("Purchase failed", message: error.localizedDescription)
            case .clientInvalid: // client is not allowed to issue the request, etc.
                return alertWithTitle("Purchase failed", message: "Not allowed to make the payment")
            case .paymentCancelled: // user cancelled the request, etc.
                return nil
            case .paymentInvalid: // purchase identifier was invalid, etc.
                return alertWithTitle("Purchase failed", message: "The purchase identifier was invalid")
            case .paymentNotAllowed: // this device is not allowed to make the payment
                return alertWithTitle("Purchase failed", message: "The device is not allowed to make the payment")
            case .storeProductNotAvailable: // Product is not available in the current storefront
                return alertWithTitle("Purchase failed", message: "The product is not available in the current storefront")
            case .cloudServicePermissionDenied: // user has not allowed access to cloud service information
                return alertWithTitle("Purchase failed", message: "Access to cloud service information is not allowed")
            case .cloudServiceNetworkConnectionFailed: // the device could not connect to the nework
                return alertWithTitle("Purchase failed", message: "Could not connect to the network")
            case .cloudServiceRevoked: // user has revoked permission to use this cloud service
                return alertWithTitle("Purchase failed", message: "Cloud service was revoked")
            default:
                return alertWithTitle("Purchase failed", message: "Please try again later.")
            }
        }
    }
    
    func alertForRestorePurchases(_ results: RestoreResults) -> UIAlertController {
        
        if results.restoreFailedPurchases.count > 0 {
            print("Restore Failed: \(results.restoreFailedPurchases)")
            return alertWithTitle("Restore failed", message: "Unknown error. Please contact support")
        } else if results.restoredPurchases.count > 0 {
            print("Restore Success: \(results.restoredPurchases)")
            return alertWithTitle("Purchases Restored", message: "All purchases have been restored")
        } else {
            print("Nothing to Restore")
            return alertWithTitle("Nothing to restore", message: "No previous purchases were found")
        }
    }
    
    func alertForVerifyReceipt(_ result: VerifyReceiptResult) -> UIAlertController {
        
        switch result {
        case .success(let receipt):
            print("Verify receipt Success: \(receipt)")
            return alertWithTitle("Receipt verified", message: "Receipt verified remotely")
        case .error(let error):
            print("Verify receipt Failed: \(error)")
            switch error {
            case .noReceiptData:
                return alertWithTitle("Receipt verification", message: "No receipt data. Try again.")
            case .networkError(let error):
                return alertWithTitle("Receipt verification", message: "Network error while verifying receipt: \(error)")
            default:
                return alertWithTitle("Receipt verification", message: "Receipt verification failed: \(error)")
            }
        }
    }
    
    func alertForVerifySubscriptions(_ result: VerifySubscriptionResult, productIds: Set<String>) -> UIAlertController {
        
        switch result {
        case .purchased(let expiryDate, let items):
            print("\(productIds) is valid until \(expiryDate)\n\(items)\n")
            return alertWithTitle("Product is purchased", message: "Product is valid until \(expiryDate)")
        case .expired(let expiryDate, let items):
            print("\(productIds) is expired since \(expiryDate)\n\(items)\n")
            return alertWithTitle("Product expired", message: "Product is expired since \(expiryDate)")
        case .notPurchased:
            print("\(productIds) has never been purchased")
            return alertWithTitle("Not purchased", message: "This product has never been purchased")
        }
    }
    
    func alertForVerifyPurchase(_ result: VerifyPurchaseResult, productId: String) -> UIAlertController {
        
        switch result {
        case .purchased:
            print("\(productId) is purchased")
            return alertWithTitle("Product is purchased", message: "Product will not expire")
        case .notPurchased:
            print("\(productId) has never been purchased")
            return alertWithTitle("Not purchased", message: "This product has never been purchased")
        }
    }
    
}


//MARK: -
//MARK: - encoding helper methods
extension SPLTSubscribeViewController {
    //: ### Base64 encoding a string
    func base64Encoded(string: String) -> String? {
        if let data = string.data(using: .utf8) {
            return data.base64EncodedString()
        }
        return nil
    }
    
    //: ### Base64 decoding a string
    func base64Decoded(string: String) -> String? {
        if let data = Data(base64Encoded: string, options: .ignoreUnknownCharacters) {
            return String(data: data, encoding: .utf8)
        }
        return nil
    }
}

#if os(iOS)
//MARK: -
//MARK: - UITableView data source methods
extension SPLTSubscribeViewController : UITableViewDataSource {
    
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if UIDevice.current.userInterfaceIdiom == .pad {
            return 260.0 //220.0
        } else {
            return UITableView.automaticDimension
        }
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return SPLTSubscriptionUtility.shared.subscriptionProducts.count
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let SPLTSubscriptionCell: SPLTSubscriptionCell = tableView.dequeueReusableCell(withIdentifier: "SPLTSubscriptionCellIdentifier", for: indexPath) as? SPLTSubscriptionCell {
            SPLTSubscriptionCell.selectionStyle = .none
            SPLTSubscriptionCell.delegate = self
                let product = SPLTSubscriptionUtility.shared.subscriptionProducts[indexPath.row]
                SPLTSubscriptionCell.setCellData(product, indexPath: indexPath)
            
            return SPLTSubscriptionCell
        } else {
            return UITableViewCell()
        }
    }
}

//MARK: - UITableView delegate methods
extension SPLTSubscribeViewController : UITableViewDelegate {
    
}

//MARK: - SPLTSubscriptionCellDelegate delegate methods
extension SPLTSubscribeViewController : SPLTSubscriptionCellDelegate {
    func didClickstartYourFreeTrail(_ subscriptionProduct: SPLTSubscriptionProduct) {
        if let appleProductId = subscriptionProduct.strAppleProductId, appleProductId != "" {
            self.purchaseSubscriptionWithProduct(subscriptionProduct: subscriptionProduct)
        } else {
            let alert = self.alertWithTitle("Revry", message: "This product can't be subscribed from this device. Please subscribe from website.")
            self.present(alert, animated: true, completion: nil)
        }
    }
}

#elseif os(tvOS)
extension SPLTSubscribeViewController {
    open func resetCollectionViewSize(_ collectionViewItemSize: CGSize) {
        if let flowLayout = self.collectionView?.collectionViewLayout as? UICollectionViewFlowLayout {
            flowLayout.itemSize = collectionViewItemSize
        }
    }
}

//MARK: - UICollectionView Data Source methods
extension SPLTSubscribeViewController : UICollectionViewDataSource {
    
    public func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if SPLTSubscriptionUtility.shared.subscriptionProducts.count > 0 {
            return SPLTSubscriptionUtility.shared.subscriptionProducts.count
                //+ 1
        } else {
            return 0
        }
    }
    
    public func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        if indexPath.row <= SPLTSubscriptionUtility.shared.subscriptionProducts.count - 1 {
            if let dsRevrySubscriptionCollectionViewCell = collectionView.dequeueReusableCell(withReuseIdentifier: "RevrySubscriptionCollectionViewCell", for: indexPath) as? RevrySubscriptionCollectionViewCell {
                    let product = SPLTSubscriptionUtility.shared.subscriptionProducts[indexPath.row]
                    dsRevrySubscriptionCollectionViewCell.delegate = self
                    dsRevrySubscriptionCollectionViewCell.setCellData(product, indexPath: indexPath)
                return dsRevrySubscriptionCollectionViewCell
            }
        } else {
            if let dsRevrySubscriptionWatchFreeCollectionViewCell = collectionView.dequeueReusableCell(withReuseIdentifier: "RevrySubscriptionWatchFreeCollectionViewCell", for: indexPath) as? RevrySubscriptionWatchFreeCollectionViewCell {
                dsRevrySubscriptionWatchFreeCollectionViewCell.delegate = self
                 return dsRevrySubscriptionWatchFreeCollectionViewCell
            }
        }
        return UICollectionViewCell()
    }
}

//MARK: - UICollectionView Delegate methods
extension SPLTSubscribeViewController : UICollectionViewDelegate {
    public func collectionView(_ collectionView: UICollectionView, canFocusItemAt indexPath: IndexPath) -> Bool {
        return false
    }
}

extension SPLTSubscribeViewController : UICollectionViewDelegateFlowLayout{
    
//    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
//        return UIEdgeInsets(top: 20, left: 0, bottom: 10, right: 0)
//    }
//
//    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
//        let collectionViewWidth = collectionView.bounds.width
//        return CGSize(width: collectionViewWidth/3, height: collectionViewWidth/3)
//    }
    
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 50
    }
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 50
    }
}

//MARK: - SPLTSubscriptionCellDelegate delegate methods
extension SPLTSubscribeViewController : RevrySubscriptionCollectionViewCellDelegate {
    func didClickstartYourFreeTrail(_ subscriptionProduct: SPLTSubscriptionProduct) {
            if let appleProductId = subscriptionProduct.strAppleProductId, appleProductId != "" {
                self.purchaseSubscriptionWithProduct(subscriptionProduct: subscriptionProduct)
            } else {
                let alert = self.alertWithTitle("Revry", message: "This product can't be subscribed from this device. Please subscribe from website.")
                self.present(alert, animated: true, completion: nil)
            }
        }
}

//MARK: - RevrySubscriptionWatchFreeCollectionViewCell Delegate methods
extension SPLTSubscribeViewController : RevrySubscriptionWatchFreeCollectionViewCellDelegate {
    func didClickWatchForFree(_ index: Int) {
        self.delegate?.didSkipToSubscribe(self)
    }
}

#endif


