import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 引入ant design vue组件与必要的全局css
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

// 记得再app中使用引入的组件
createApp(App).use(store).use(router).use(Antd).mount('#app')
