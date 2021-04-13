<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">

      <a-menu
              mode="inline"
              :style="{ height: '100%', borderRight: 0 }"
              @click="handleClick"
              :openKeys="openKeys"
      >
        <!-- 动态树形菜单  -->
        <a-menu-item key="welcome">
          <MailOutlined />
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <!-- 主菜单 -->
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <!--二级菜单-->
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item key="tip" :disabled="true">
          <span>以上菜单在分类管理配置</span>
        </a-menu-item>
      </a-menu>

    </a-layout-sider>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="ifShowWelcome">
        欢迎来到一飞的Wiki！
      </div>

      <!-- 电子书列表 -->
      <a-list v-show="!ifShowWelcome" item-layout="vertical" size="large" :data-source="ebooks"
                                                  :grid="{ gutter: 20, column: 3}">

        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.name }}</a>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>

    </a-layout-content>
  </a-layout>

</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  import { message } from 'ant-design-vue';
  import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
  import {Tool} from "@/util/tool";
  const listData: Record<string, string>[] = [];


  export default defineComponent({
    name: 'Home',
    components: {
      StarOutlined,
      LikeOutlined,
      MessageOutlined,
    },

    // vue3新增，初始化方法
    setup: function () {
      const ebooks = ref();

      onMounted(() => {
        handleQueryCategory();
        axios.get('/ebook/get', {
          params: {
            page: 1,
            size: pagination.pageSize
          }
        }).then((response) => {
                  if(response.data.success){
                    ebooks.value = response.data.content.records;
                  } else {
                    message.error(response.data.message);
                  }
                });
      });

      const pagination = {
        onChange: (page: number) => {
          console.log(page);
        },
        pageSize: 1000,
      };

      const actions: Record<string, string>[] = [
        { type: 'StarOutlined', text: '156' },
        { type: 'LikeOutlined', text: '156' },
        { type: 'MessageOutlined', text: '2' },
      ];

      const level1 = ref();
      const handleQueryCategory = () => {
        axios.get('/category/all').then((response) => {
          if(response.data.success){
            level1.value= [];

            level1.value = Tool.array2Tree(response.data.content,0);
          } else{
            message.error("分类信息加载失败！");
          }
        });
      };

      const ifShowWelcome = ref(true);
      const handleClick = (value: any) => {
        // console.log("menu click", value)
        if (value.key === 'welcome') {
          ifShowWelcome.value = true;
        } else {
          ifShowWelcome.value = false;
        }
      };

      // 注意这里要将变量返回给页面
      return {
        ebooks,
        pagination,
        actions,

        level1,

        ifShowWelcome,
        handleClick,
      };
    }


  });
</script>

<!--scoped表示样式只在当前组件中生效-->
<style scoped>
    .ant-avatar{
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>
