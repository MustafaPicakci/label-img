var DashboardApp = {
        data() {
            return {
                image: [],
                imgId: null,
                imgName: null,
                imgDirectory: null,
                showTags: [],
                allTags: [],
                selectedDirectory_id: 0,
                loader: true,
                checkNewImages: false,
                threadIsEnd: false,
                error:null
            };
        },
        name: "DashboardApp",
        created() {


        },
        watch: {
            checkNewImages: function() {
                this.init();
            }
        },
        mounted() {
            this.init();
            this.websocket();


        },
        methods: {

            init() {
                this.checkNewImages = false;           
                
                $.getJSON('http://localhost:8080/active_directory', function(data) {}).then((data) => {
                    this.selectedDirectory_id = data;

                    $.get('http://localhost:8080/images', {
                        id: this.selectedDirectory_id
                    }, function(data) {

                    }).then((data) => {
                        this.image = data;

                        this.loader = false;
                    })
                })
            },
            getAllTags() {
                $.getJSON('http://localhost:8080/getAllTags', function(data) {

                }).then((data) => {
                    this.allTags = data.map(x => x.tags);
                })

            },
            addTag(index, c) {
                this.getAllTags();

                if (c == 0) {
                    this.imgId = this.image[index].id;
                    this.imgName = this.image[index].name;
                    this.imgDirectory = this.image[index].directory;
                }
                $.getJSON('http://localhost:8080/showTags', {
                    id: this.imgId,
                    directory: this.imgDirectory,
                    name: this.imgName
                }, function(data) {

                }).then((data) => {
                    this.showTags = data;
                })
            },
            delTag(item, index) {

                Swal.fire({
                    title: 'Etiketin bu görsel ile ilişkisi silinecek?',
                    text: "",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Evet, sil!',
                    cancelButtonText: 'Vazgeç'
                }).then((result) => {
                    if (result.value) {
                        $.get('http://localhost:8080/delTag', {
                            tag_id: item.tag_id,
                            image_id: item.image_id,
                            tags: item.tags
                        })
                        this.showTags.splice(index, 1);
                        this.getAllTags();
                    }
                })


            },
            saveTag() {
                var imgTag = document.getElementById("inputArea").value;
                if (imgTag == '') {
                    alert("Etiket alanını doldurmayı unuttunuz.", "Bu görseli tanımlayan bir etiket yazmayı deneyin.", "error");
                } else {

                    $.post("http://localhost:8080/tags", {
                        name: this.imgName,
                        image_id: this.imgId,
                        tags: imgTag
                    });

                    document.getElementById('inputArea').value = '';
                    setTimeout(() => {
                        this.addTag(null, 1);
                    }, 500);
                }
                this.getAllTags();
            },
            autoComplete() {
                autocomplete(document.getElementById("inputArea"), this.allTags);
            },
            delImg(item, index) {

                Swal.fire({
                    title: 'Bu görsel silinecek?',
                    text: "",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Evet, sil!',
                    cancelButtonText: 'Vazgeç'
                }).then((result) => {
                    if (result.value) {
                        $.get('http://localhost:8080/removeImage', {
                            image_id: item.id,
                            directory: item.directory,
                            name: item.name
                        })
                        this.getAllTags();

                        this.image.splice(index, 1);
                        Swal.fire(
                            'Silindi!',
                            'Görsel kaldırıldı.',
                            'success'
                        )
                    }
                })

            },
            websocket() {
                var socket = null;
                this.socket = new SockJS("http://localhost:8080/websocket-example");
                this.stompClient = Stomp.over(this.socket);

                this.stompClient.connect({},
                    frame => {
                        this.stompClient.subscribe("/topic/control", tick => {
                            
                            this.checkNewImages = tick.body;

                        });
                        
                      this.stompClient.subscribe("/topic/threadIsEnd", tick => {
                           
                    	  this.threadIsEnd = tick.body;

                        });
                        this.stompClient.subscribe("/topic/error", tick => {
                           
                        	this.error=true;
                        	Swal.fire(
                                    'Oops...!',
                                    'Lütfen geçerli bir dizin giriniz.',
                                    'error'
                                )                           
                          });
                    },
                    error => {
                        console.log(error);

                    }
                );
            },
            openContainingFolder() {
                $.get('http://localhost:8080/openContainingFolder', {})
            },
            simpleAlert(text){
            	if(this.error == null){
            		alert(text);
            	}
            	
            }
        },
    template: `
   
<div id="images" class="element-wrapper " >
	<div class="spinner" v-if="this.loader">
		<div class="cube1"></div>
		<div class="cube2"></div>
	</div>
	<template v-if="this.threadIsEnd">
	{{this.simpleAlert("Belirtilen dizindeki tüm görseller yüklendi.")}}
	{{this.threadIsEnd=false}}
	</template>
	<div class="item" v-for="(item,index) in image" style="display:inline-block; z-index:-1;">
		<a href="#" @click="addTag(index,0)" data-toggle="modal" data-target="#de-modal">
			<img id="img" v-bind:src="'data:image/jpg;base64,' + item.data" />
		</a>
		<button @click=delImg(item,index) id="btn_del" class="btn btn-danger">X</button>
		<div class="modal fade" id="de-modal">
			<!-- fade  bir efekt -->
			<div class="modal-dialog modal-dialog-centered modal-md">
				<div class="modal-content">
					<div class="modal-header">
						<button class="close" data-dismiss="modal">&times;</button>
					</div>
					<!-- model-header -->
					<div class="modal-body">
						<div class="container text-center">
							<br>
								<br>
									<template>
										<table class="table table-hover " >
											<thead class="thead">
												<tr>
													<th scope="col">Image Name</th>
													<th scope="col">Open From Folder</th>
												</tr>
											</thead>
											<tbody>
												<template>
													<tr>
														<td>{{imgName}}</td>
														<td>
															<button @click=openContainingFolder() type="button">Open</button>
														</td>
													</tr>
												</template>
											</tbody>
										</table>
										<br>
											<br>
												<br>
												</template>
												<table class="table table-hover " >
													<thead class="thead">
														<tr>
															<th scope="col">Image Labels</th>
															<th scope="col">Delete Label</th>
														</tr>
													</thead>
													<template v-for="(item,index) in showTags">
														<tbody>
															<tr>
																<td>{{item.tags}}</td>
																<td>
																	<button @click=delTag(item,index) type="button" class="delete-button" aria-label="Close" style="">
																		<span aria-hidden="true">&times;</span>
																	</button>
																</td>
															</tr>
														</tbody>
													</template>
												</table>
												<div class="autocomplete text-center">
													<input type="text" id="inputArea" placeholder="insert tag" v-on:keyup.enter="saveTag()" v-on:keypress="autoComplete()"></input>
													<br>
														<br>
															<button class="btn btn-primary" @click=saveTag()>Kaydet</button>
														</div>
													</div>
												</div>
												<!-- model-body -->
												<div class="modal-footer">
													<button id="closeBtn" class="btn btn-danger" data-dismiss="modal">Close</button>
												</div>
												<!-- model-footer -->
											</div>
											<!-- model-content -->
										</div>
										<!-- modal-dialog -->
									</div>
									<!-- modal -->
								</div>
							</div>
  `
};