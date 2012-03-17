(function($) {
	
	userHB = {};
	
	userHB.searchUsers = function(){
		$.ajax({
			type: 'POST',
			url : 'engine/users/searchUsers',
			dataType : 'json',
			success: function(){
				location.reload();
			}
		});
	}
	
	
})(jQuery);
