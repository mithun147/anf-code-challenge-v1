/*******************************************************************************
 * Copyright 2016 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function($) {
    "use strict";

    $('#new_form').on('submit', function(e) {
		e.preventDefault();
      // hide previous messages
		$("#error-message").hide();
		$("#success-message").hide();

		let form = $(this);
		let actionUrl = form.attr('action');

		$.ajax({
			type : "GET",
			url : actionUrl,
			data : form.serialize(), 
			success : function(data) {
				$("#success-message").show();
			},
			error : function(jqXHR) {
				$("#error-message").text(jqXHR.responseText);
				$("#error-message").show();
			}
		});

	});

})(jQuery);