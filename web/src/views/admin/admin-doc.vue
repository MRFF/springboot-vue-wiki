<template>


  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <p>
        <a-form layout="inline"> <!--:model=""> -->
<!--          <a-form-item>-->
<!--            <a-input v-model:value="searchParams.name" placeholder="名称"/>-->
<!--          </a-form-item>-->
<!--          <a-form-item>-->
<!--            <a-button type="primary" @click="handleQuery({page:1, size: pagination.pageSize, name: searchParams.name})">-->
<!--              查询-->
<!--            </a-button>-->
<!--          </a-form-item>-->
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
              :data-source="level1"
              :loading="loading"
              :pagination="false"
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
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
<!--      <a-form-item label="分类">-->
<!--        <a-cascader-->
<!--                v-model:value="docIds"-->
<!--                :field-names="{ label: 'name', value: 'id', children: 'children' }"-->
<!--                :options="level1"-->
<!--        />-->
<!--      </a-form-item>-->
      <a-form-item label="父分类">
        <a-select
                v-model:value="doc.parent"
                ref="select"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="item in level1" :key="item.id" :value="item.id" :disabled="item.id === doc.id">
            {{item.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="排序">
        <a-input v-model:value="doc.sort" type="textarea" />
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
    name: 'AdminDoc',
    setup: function () {
      const docs = ref();
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

      // const searchParams = ref({});
      const level1 = ref();

      const handleQuery = () => {
        loading.value = true;
        axios.get('/doc/all').then((response) => {
                  loading.value = false;
                  if(response.data.success){
                    docs.value = response.data.content;

                    level1.value = [];
                    level1.value = Tool.array2Tree(docs.value,0);
                  } else{
                    message.error("分类信息加载失败！");
                  }
        });
      };


      // 编辑对话框用到的变量
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 用以存储当前行的数据
      const doc = ref({});
      const handleModalOk = () => {
        modalLoading.value = true;
        axios.post('/doc/save', doc.value)
             .then((response) => {
               modalLoading.value = false;
               if(response.data.success){
                 modalVisible.value = false;

                 handleQuery();
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
        doc.value = Tool.copy(record);
      };

      /**
       * 新增
       */
      const add = () => {
        modalVisible.value = true;
        modalLoading.value = false;
        doc.value = {};
      };

      /**
       * 删除
       */
      const del = (id : string) => {
        axios.delete('/doc/delete/' + id)
             .then((response) => {
               if(response.data.success){
                 handleQuery();
               }
        });
      };

      onMounted(() => {
        // 页面刚加载，应该查询第一页的数据
        handleQuery();
      });

      return {
        docs,
        level1,
        columns,
        loading,

        edit,
        add,
        del,

        modalVisible,
        modalLoading,
        handleModalOk,
        doc,

        handleQuery,
      };

    }
  });
</script>
