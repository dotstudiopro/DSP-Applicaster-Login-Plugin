//
//  SPLTSubscriptionCell.swift
//  RevryApp-iOS
//
//  Created by Anwer on 1/31/19.
//  Copyright © 2019 Dotstudioz. All rights reserved.
//

import UIKit
//import DotstudioPRO

protocol SPLTSubscriptionCellDelegate {
    func didClickstartYourFreeTrail(_ subscriptionProduct: SPLTSubscriptionProduct)
}

class SPLTSubscriptionCell: UITableViewCell {
    @IBOutlet weak var lableName: UILabel?
    @IBOutlet weak var lablePrice: UILabel?
    @IBOutlet weak var lableDescription: UILabel?
    @IBOutlet weak var lableMostPopular: UILabel?
    @IBOutlet weak var startFreeTrialButton: UIButton?
    @IBOutlet weak var mostPopularLableHeightConstraint: NSLayoutConstraint?
    
    var delegate: SPLTSubscriptionCellDelegate?
    var subscriptionProduct:SPLTSubscriptionProduct?

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.lablePrice?.text = ""
        self.lableDescription?.text = ""
        self.lableName?.text = ""
    }

    func setCellData(_ subscriptionProduct: SPLTSubscriptionProduct, indexPath:IndexPath) {
        self.subscriptionProduct = subscriptionProduct
    
        if let durationDict = subscriptionProduct.durationDict {
            if let interval = durationDict["interval"] as? Int {
                if let interval_unit = durationDict["interval_unit"] as? String {
                    if let strPrice = subscriptionProduct.strPrice, let dPrice = Double(strPrice) {
                        if subscriptionProduct.strPriceDisplay == "monthly"{
                            let monthlyPrice = "\(dPrice/Double(interval)))"
                            self.lablePrice?.text = "$ \(monthlyPrice.prefix(4)) / \(interval_unit)"
                        }else{
                            var strInterval = ""
                            if interval == 12{
                                strInterval = "year"
                            }else if interval == 1{
                                strInterval = "\(interval_unit)"
                            }else{
                                strInterval = "\(interval) \(interval_unit)"
                            }
                            self.lablePrice?.text = "$ \(strPrice) / \(strInterval)"
                        }
                    }
                }
            }
        }
        
        if let strDescription = subscriptionProduct.strDescription {
            self.lableDescription?.text = strDescription
        }
        
        if let strName = subscriptionProduct.strName {
            self.lableName?.text = strName
        }
        
        if subscriptionProduct.isMostPopular {
            self.lableMostPopular?.isHidden = false
            self.mostPopularLableHeightConstraint?.constant = 25
        } else {
            self.lableMostPopular?.isHidden = true
            self.mostPopularLableHeightConstraint?.constant = 0
        }
    }
    
    @IBAction func startYourFreeTrailbtnAction(_ sender: UIButton) {
        print("Start your free trail : \(sender.tag)")
        if let subscriptionProduct = self.subscriptionProduct {
            self.delegate?.didClickstartYourFreeTrail(subscriptionProduct)
        }
    }
    /// Rounds the double to decimal places value
    func rounded(toPlaces places:Int, value:Double) -> Double {
        let divisor = pow(10.0, Double(places))
        return (value * divisor).rounded() / divisor
    }
}
