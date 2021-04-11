import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 引入ant design vue组件与必要的全局css
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';

// 记得再app中使用引入的组件
const app = createApp(App).use(store).use(router).use(Antd);
app.mount('#app');

const icons: any = Icons;
for(const icon in icons){
    app.component(icon, icons[icon]);
}