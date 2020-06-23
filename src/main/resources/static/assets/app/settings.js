var SettingsApp = {
        data() {
            return {
                allTags: [],
                directories: [],
                selectedDirectory: null
            };
        },
        name: "SettingsApp",
        mounted() {
            this.init();
        },
        watch: {},
        methods: {
            init() {
                this.getAllTags();
                this.getDirectories();
            },
            getAllTags() {
                $.getJSON('http://localhost:8080/getAllTags', {}, function(data) {

                }).then((data) => {
                    this.allTags = data;

                })
            },
            removeTag(item, index) {

                Swal.fire({
                    title: 'Bu etiketi silmek üzeresiniz ...',
                    text: "Bu işlem etiketin tüm görseller ile ilişkisinin silinmesine sebep olacak!",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Evet, sil!',
                    cancelButtonText: 'Vazgeç'
                }).then((result) => {
                    if (result.value) {
                        $.get('http://localhost:8080/removeTag', {
                            id: item.id,
                        })
                        this.allTags.splice(index, 1);
                    }
                })

            },
            removeDirectory(item, index) {

                Swal.fire({
                    title: 'Bu dizini silmek üzeresiniz ...',
                    text: "Bu işlem, dizindeki görseller ve etiketlerinin silinmesine sebep olacak !",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Evet, sil!',
                    cancelButtonText: 'Vazgeç'
                }).then((result) => {
                    if (result.value) {
                        $.get('http://localhost:8080/removeDirectory', {
                            id: item.id,
                        })
                        this.directories.splice(index, 1);
                    }
                })
            },
            getDirectories() {
                $.getJSON('http://localhost:8080/directories', {}, function(data) {

                }).then((data) => {

                    this.directories = data;
                })

                this.directories.forEach(element => {
                    if (element.is_active == 1) {
                        this.selectedDirectory = element.directory;
                    }
                });
            },
            beActive(item, index) {
                $.getJSON('http://localhost:8080/beActive', {
                    id: item.id
                }, function(data) {

                }).then((data) => {

                    this.directories = data;

                })

                this.directories.forEach(element => {
                    if (element.is_active == 1) {
                        this.selectedDirectory = element.directory;
                    }

                });
            }
        },
  template: `
<div class="element-wrapper">
	<div class="container mt-4">
		<table class="table table-hover ">
			<thead class="thead-dark text-center">
				<tr>
					<th scope="col">Tag ID</th>
					<th scope="col">Tag</th>
					<th scope="col"></th>
				</tr>
			</thead>
			<tbody class="text-center">
				<template v-for="(item,index) in allTags">
					<tr>
						<td>{{item.id}}</td>
						<td>{{item.tags}}</td>
						<td>
							<button @click=removeTag(item,index) type="button" class="btn btn-danger" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</td>
					</tr>
				</template>
			</tbody>
		</table>
		<table class="table table-hover">
			<thead class="thead-dark text-center">
				<tr>
					<th scope="col">Directory ID</th>
					<th scope="col">Directory</th>
					<th scope="col">Status</th>
					<th scope="col"></th>
				</tr>
			</thead>
			<tbody class="text-center">
				<template v-for="(item,index) in directories">
					<tr>
						<td>{{item.id}}</td>
						<td>{{item.directory}}</td>
						<td v-if="item.is_active>0">
							<button @click=beActive(item,index) type="button" class="btn btn-success" aria-label="Close">
                           Active
                            </button>
						</td>
						<td v-else>
							<button @click=beActive(item,index) type="button" class="btn btn-danger" aria-label="Close">
                           Deactive
                            </button>
						</td>
						<td>
	  						<button @click=removeDirectory(item,index) type="button" class="delete-button" aria-label="Close" style="">
	  						<span aria-hidden="true">&times;</span>
	  						</button>
	  					</td>
					</tr>
				</template>
			</tbody>
		</table>
	</div>
</div>
  `
};
