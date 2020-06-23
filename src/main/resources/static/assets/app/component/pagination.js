Vue.component("pagination",{
	props:["url" ,"item_per_page","term","display_page_size"],
	data(){
		return {
			data:[],
			raw_data:{},
			total_page:1,
			current_page:1,
			display_page_number:20,
			display_pages:[]
		};
	},
	mounted(){
	    if(this.display_page_size){
	        this.display_page_number=this.display_page_size;
	    }
		this.get_page();
	},
	watch: {
		term(n){
			this.get_page(1);
		},
		url(){
			this.get_page(1);
		},
	    data: {
	        handler: function() {
	        	this.$emit('watcher', [this.data,this.raw_data]);
	        	this.calculate_page_range_view();
	        },
	        deep: true
	    }
	},
	methods:{
		prepare_url(){
			return this.url
			.replace("{term}",!this.term?"":this.term)
			.replace("{limit}",this.item_per_page)
			.replace("{offset}",(this.item_per_page*(this.current_page-1)));
		},
		 get_page(page){
			if(page){
				this.current_page=page;
			}
			if(this.current_page<=0){
				this.current_page=1;
				return;
			}
			if(this.total_page>0 && this.current_page>this.total_page){
				this.current_page=this.total_page;
				return;
			}
			  
			var url=this.prepare_url();
			  
			ajax.get(url,r=>{
			    this.raw_data=r;
				this.data=r.result;
				this.current_page=r.current;
				this.has_next=r.hasNext;
				this.total_page=r.count;
			});
		  },
		  get_next(){
			  this.current_page++;
			  this.get_page();
		  },
		  get_prev(){
			  this.current_page--;
			  this.get_page();
		  },
		  calculate_page_range_view(){
		      this.display_pages=[];
		      var center = this.display_page_number/2;
		      var from_page_number=this.current_page-center;
		      var to_page_number=this.current_page+center;
		      if(from_page_number<=0){
		          from_page_number=1;
		          to_page_number=this.display_page_number;
		      }
		      if(to_page_number>=this.total_page){
		          to_page_number=this.total_page;
		      }
		      if(this.total_page<=this.display_page_number){
		          from_page_number=1;
		          to_page_number=this.total_page;
		      }
		      
		      
		      for(var i =from_page_number;i<=to_page_number;i++){
                  this.display_pages.push(i);
              }
		      
		  }
	},
	template:`
<div>
<nav>
  <ul class="pagination">
    <li @click="get_prev" class="page-item"><a class="page-link" href="javascript:;"><i class="fa fa-chevron-left"></i></a></li>
    <li v-for="i in display_pages" @click="get_page(i)" :class="{'active':i==current_page}" class="page-item"><a class="page-link" href="javascript:;">{{i}}</a></li>
    <li @click="get_next" class="page-item"><a class="page-link" href="javascript:;"><i class="fa fa-chevron-right"></i></a></li>
  </ul>
</nav>
</div>
	`
});