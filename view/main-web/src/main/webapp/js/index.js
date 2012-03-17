(function($) {

	$(window).hashchange(function() {
		if(!location.hash || location.hash == '#!') {
			return;
		}
		var hash = location.hash.substring(1);
		$.holy('./templates/' + hash + '.xml');
	});

	$.ajaxSetup({
		headers: { "Accept": "application/json;charset=utf-8" },
		error : function() {
			$.holy("templates/error.xml", {});
		}
	});
	
	usuarioLogado = {};
	
	montaObjetoUsuario = function() {
		$.ajax({
			type : 'GET',
			url : 'engine/users/logado',
			dataType : 'json',
			async: false,
			success : function(usu){
				usuarioLogado = usu;
			}
		});

	}	
	
	$(document).ready(function() {

		montaObjetoUsuario();	
		
		$.holy('./templates/menu.xml', { usuarioLogado : usuarioLogado });
		$.holy('./templates/userbar.xml', { usuarioLogado : usuarioLogado });
		$('.message').messageMonitor();				
		
		$(window).hashchange();
	});
	
})(jQuery);
