const routes = [
    { path: '/settings', component: SettingsApp },
    { path: '/', component: DashboardApp },
    { path: '/imgByTag/:tag', component: imgByTagApp}
];
const router = new VueRouter({
  routes
});

const app = new Vue({
    el:"#app",
    name:"root",
    router,
    data:{
    },
    created(){     
        this.init();
    },
   
    methods:{
        init(){
        }
    }
});
