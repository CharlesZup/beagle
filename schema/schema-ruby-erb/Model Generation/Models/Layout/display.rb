#   Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA

#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
 
#       http://www.apache.org/licenses/LICENSE-2.0
 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

require_relative '../../Synthax/variable.rb'
require_relative '../base_component.rb'

class Display < BaseComponent

    # todo: display is an enum, we have to figure out how to represent this in ruby
    def initialize
        textVariables = [
            Variable.new(:name => "backgroundColor", :typeName => "String", :isOptional => true)
        ]
        synthaxType = SynthaxType.new(
            :kind => 'struct',
            :name => self.name,
            :variables => textVariables,
            :package => "br.com.zup.beagle.widget.core"
        )

        super(synthaxType)

    end

end