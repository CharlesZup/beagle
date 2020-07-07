//
//  BeagleConfig.swift
//  AutomatedTests
//
//  Created by Lucas Sousa Silva on 07/07/20.
//  Copyright © 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA. All rights reserved.
//

import Beagle
import Foundation

class BeagleConfig {
    static func config() {
        
        let dependencies = BeagleDependencies()
        dependencies.urlBuilder = UrlBuilder(
            baseUrl: URL(string: "http://192.168.100.71:8080/")
        )
        Beagle.dependencies = dependencies
    }
}
