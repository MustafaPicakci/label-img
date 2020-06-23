'use strict';

 
function is_display_type(display_type) {
  return $('.display-type').css('content') == display_type || $('.display-type').css('content') == '"' + display_type + '"';
}
function not_display_type(display_type) {
  return $('.display-type').css('content') != display_type && $('.display-type').css('content') != '"' + display_type + '"';
}

// Initiate on click and on hover sub menu activation logic
function os_init_sub_menus() {

  // INIT MENU TO ACTIVATE ON HOVER
  var menu_timer;
  $('.menu-activated-on-hover').on('mouseenter', 'ul.main-menu > li.has-sub-menu', function () {
    var $elem = $(this);
    clearTimeout(menu_timer);
    $elem.closest('ul').addClass('has-active').find('> li').removeClass('active');
    $elem.addClass('active');
  });

  $('.menu-activated-on-hover').on('mouseleave', 'ul.main-menu > li.has-sub-menu', function () {
    var $elem = $(this);
    menu_timer = setTimeout(function () {
      $elem.removeClass('active').closest('ul').removeClass('has-active');
    }, 30);
  });

  // INIT MENU TO ACTIVATE ON CLICK
  $('.menu-activated-on-click').on('click', 'li.has-sub-menu > a', function (event) {
    var $elem = $(this).closest('li');
    if ($elem.hasClass('active')) {
      $elem.removeClass('active');
    } else {
      $elem.closest('ul').find('li.active').removeClass('active');
      $elem.addClass('active');
    }
    return false;
  });
}

$(function () {
   
  // #11. MENU RELATED STUFF

  // INIT MOBILE MENU TRIGGER BUTTON
  $('.mobile-menu-trigger').on('click', function () {
    $('.menu-mobile .menu-and-user').slideToggle(200, 'swing');
    return false;
  });

  os_init_sub_menus();
 
  // #16. OUR OWN CUSTOM DROPDOWNS
  $('.os-dropdown-trigger').on('mouseenter', function () {
    $(this).addClass('over');
  });
  $('.os-dropdown-trigger').on('mouseleave', function () {
    $(this).removeClass('over');
  });

  // #17. BOOTSTRAP RELATED JS ACTIVATIONS

  // - Activate tooltips
  $('[data-toggle="tooltip"]').tooltip();

  // - Activate popovers
  $('[data-toggle="popover"]').popover();
 
 

  // #19. Fancy Selector
  $('.fs-selector-trigger').on('click', function () {
    $(this).closest('.fancy-selector-w').toggleClass('opened');
  });
  
});

function elementActionFoldInit() {
	  setTimeout(()=>{
		  $('.element-action-fold').on('click',function(){
			  	var $wrapper = $(this).closest('.element-wrapper');
			    $wrapper.find('.element-box-tp, .element-box').toggle(0);
			    var $icon = $(this).find('i');

			    if ($wrapper.hasClass('folded')) {
			      $icon.removeClass('os-icon-plus-circle').addClass('os-icon-minus-circle');
			      $wrapper.removeClass('folded');
			    } else {
			      $icon.removeClass('os-icon-minus-circle').addClass('os-icon-plus-circle');
			      $wrapper.addClass('folded');
			    }
			    return false;
		  });
	  },1000);
}

//back to pot button
$(document).ready(function() {
	$('#backTop').backTop({
	  'position' : 400,
	  'speed' : 500,
	  'color' :'red',
	});
});


//form submit
//directory
$(document).ready(function() {
    $("#frm_directory").submit(function(e) {
        e.preventDefault();

        var directory = document.getElementById("directory").value;

        $.post("http://localhost:8080/newImages", {
            d: directory,
        });

        Swal.fire({
            icon: 'success',
            title: 'İşlem Başladı.',
            timer: 1500
        })

        document.getElementById("directory").value = "";
    });


    //o etikete sahip bir görsel db de yoksa post işlemi durdurularak imgbytag componentinin yüklenmesi engelleniyor
    $("#frm_tag").submit(function(e) {
        e.preventDefault();

        var tag = document.getElementById("tag").value;


        $.getJSON('http://localhost:8080/img', {
            tag: tag
        }, function(data) {
            if (Object.keys(data).length >= 1) {
                e.currentTarget.submit();
            } else {
                alert("Bu etikete sahip bir görsel bulunamadı", "", "error");
                document.getElementById("tag").value = "";
            }
        });

    });
});