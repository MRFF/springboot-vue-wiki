<template>


  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <p>
        <a-form layout="inline" :model="searchParams">
          <a-form-item>
            <a-input v-model:value="searchParams.name" placeholder="名称"/>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page:1, size: pagination.pageSize, name: searchParams.name})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="categorys"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="del(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>

    </a-layout-content>
  </a-layout>

  <!-- Vue3的template下可以放置多个标签，Vue2则不支持 -->
  <a-modal
          title="分类表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <!-- 模态框内的表单，用以编辑 -->
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
<!--      <a-form-item label="分类">-->
<!--        <a-cascader-->
<!--                v-model:value="categoryIds"-->
<!--                :field-names="{ label: 'name', value: 'id', children: 'children' }"-->
<!--                :options="level1"-->
<!--        />-->
<!--      </a-form-item>-->
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent" />
      </a-form-item>
      <a-form-item label="排序">
        <a-input v-model:value="category.sort" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import { message } from 'ant-design-vue';
  import axios from 'axios';
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name: 'AdminCategory',
    setup: function () {
      const categorys = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
          key: 'parent',
          dataIndex: 'parent'
        },
        {
          title: '排序',
          key: 'sort',
          dataIndex: 'sort'
        },

        {
          title: 'Action',
          key: 'action',
          slots: {customRender: 'action'}
        }
      ];

      const searchParams = ref({});

      const handleQuery = (params: any) => {
        loading.value = true;
        // GET请求需要传入params参数，POST请求则无此限制
        axios.get('/category/get', {
          params: {
            page: params.page,
            size: params.size,
            name: params.name,
          }
        }).then((response) => {
                  loading.value = false;
                  const data = response.data;
                  categorys.value = data.content.records;
                  // 重置分页按钮
                  pagination.value.current = params.page;
                  pagination.value.total = data.content.total;
        });
      };

      const handleTableChange = (pagination : any) => {
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      // 编辑对话框用到的变量
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 用以存储当前行的数据
      const category = ref({});
      const handleModalOk = () => {
        modalLoading.value = true;
        axios.post('/category/save', category.value)
             .then((response) => {
               modalLoading.value = false;
               if(response.data.success){
                 modalVisible.value = false;

                 handleQuery({
                   page: pagination.value.current,
                   size: pagination.value.pageSize
                 });
               } else {
                 message.error(response.data.message);
               }
             });

      };

      /**
       * 编辑
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        category.value = Tool.copy(record);
      };

      /**
       * 新增
       */
      const add = () => {
        modalVisible.value = true;
        modalLoading.value = false;
        category.value = {};
      };

      /**
       * 删除
       */
      const del = (id : string) => {
        axios.delete('/category/delete/' + id)
             .then((response) => {
               if(response.data.success){
                 handleQuery({
                   page: pagination.value.current,
                   size: pagination.value.pageSize
                 });
               }
        });
      };

      onMounted(() => {
        // 页面刚加载，应该查询第一页的数据
        handleQuery({
          page: 1,
          size: pagination.value.pageSize
        });
      });

      return {
        categorys,
        pagination,
        columns,
        loading,
        handleTableChange,

        edit,
        add,
        del,

        modalVisible,
        modalLoading,
        handleModalOk,
        category,

        searchParams,
        handleQuery,
      };

    }
  });
</script>
