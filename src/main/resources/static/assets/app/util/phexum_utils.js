Vue.filter('formatDate', function(value) {
  if (value) {
	  var d = new Date(value),
      month = '' + (d.getMonth() + 1),
      day = '' + d.getDate(),
      year = d.getFullYear();

	  if (month.length < 2) month = '0' + month;
	  if (day.length < 2) day = '0' + day;
	
	  return [day, month, year].join('.');
  }
});

Vue.filter('formatTime', function(value) {
  if (value) {
	  var d = new Date(value),
      hours =   d.getHours(),
      minutes = d.getMinutes();

	  if (hours < 10) hours = '0' + hours;
	  if (minutes<10) minutes = '0' + minutes;
	
	  return [hours,minutes].join(':');
  }
});
Vue.filter('timeSince', function(value) {
  if (value) {
	  var seconds = Math.floor((new Date() - new Date(value)) / 1000);
	  var interval = Math.floor(seconds / 31536000);

	  if (interval > 1) {
	    return interval + " yıl önce";
	  }
	  interval = Math.floor(seconds / 2592000);
	  if (interval > 1) {
	    return interval + " ay önce";
	  }
	  interval = Math.floor(seconds / 86400);
	  if (interval > 1) {
	    return interval + " gün önce";
	  }
	  interval = Math.floor(seconds / 3600);
	  if (interval > 1) {
	    return interval + " saat önce";
	  }
	  interval = Math.floor(seconds / 60);
	  if (interval > 1) {
	    return interval + " dakika önce";
	  }
	  return Math.floor(seconds) + " saniye önce";
  }
});
 
/*
 *Start Phexum Scripts 
 */
var ajax={
		post:function(url,data,cb,err){
			$.ajax({
				  url:url,
				  data:JSON.stringify(data),
				  type:"POST",
				  dataType: 'json',
				  headers: {'Accept': 'application/json','Content-Type': 'application/json'},
				  success:cb||function(){},
				  error:err||function(){}
			  });
		},
		get:function(url,cb,err){
			$.ajax({url:url,success:cb||function(){},error:err||function(){}});
		}
};

function alert(title,text="",type="success"){
	Swal.fire(title,text,type);
}
function error(title,text=""){
	alert(title,text,"error");
}
function warning(title,text=""){
	alert(title,text,"warning");
}

function confirm(text,cb=function(){},type='question'){
	Swal.fire({
		  title: 'Emin misiniz?',
		  text: text,
		  type: type,
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Evet',
		  cancelButtonText: 'Hayır'
		}).then((result) => {
		  if (result.value) {
			  cb.apply();
		  }
		});
}

/*
 *End Phexum Scripts 
 */