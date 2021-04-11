import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 引入ant design vue组件与必要的全局css
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
// 导入ant design vue的图标库
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';

// 记得再app中使用引入的组件
const app = createApp(App).use(store).use(router).use(Antd);
app.mount('#app');

// 导入ant design vue的所有图标
const icons: any = Icons;
for(const icon in icons){
    app.component(icon, icons[icon]);
}

// 为axios库设置url访问的base url，之后只需要直接访问接口名即可，不必与domain拼接
axios.defaults.baseURL = process.env.VUE_APP_SERVER;

console.log('Environment: ', process.env.NODE_ENV);
console.log('Server: ', process.env.VUE_APP_SERVER);

axios.interceptors.request.use(function(config){
    console.log('Parameters: ', config);
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function(response){
    console.log('Return: ', response);
    return response;
}, error => {
    return Promise.reject(error);
});