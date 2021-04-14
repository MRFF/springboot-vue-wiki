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
                    @confirm="showConfirm(record.id)"
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

        <a-tree-select
                v-model:value="doc.parent"
                tree-data-simple-mode
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :tree-data="treeSelectData"
                placeholder="选择父文档"
                :load-data="onLoadData"
                :replaceFields="{title: 'name', key: 'id', value:'id'}"
        />

      </a-form-item>
      <a-form-item label="排序">
        <a-input v-model:value="doc.sort" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
  import {defineComponent, onMounted, ref, createVNode } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import axios from 'axios';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";

  export default defineComponent({
    name: 'AdminDoc',
    setup: function () {
      const docs = ref();
      const loading = ref(false);
      const route = useRoute();

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
        // 清空树形数据，确保重新加载后数据正常刷新
        level1.value = [];
        axios.get('/doc/get?ebookId=' + route.query.ebookId).then((response) => {
                  loading.value = false;
                  if(response.data.success){
                    docs.value = response.data.content.records;

                    level1.value = [];
                    level1.value = Tool.array2Tree(docs.value,0);
                  } else{
                    message.error("文档信息加载失败！");
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
       * 将树中某一节点及其子节点的disabled属性全部设置为true
       */
      const setChildrenDisable = (treeData: any, id:any) => {
        for(let i=0;i<treeData.length;i++){
          const current = treeData[i];
          // 如果当前节点就是要找的节点
          if(current.id === id){
            current.disabled = true;

            if(Tool.isNotEmpty(current.children)){
              // 遍历其孩子节点，将当前节点的孩子的disabled全部置为disabled
              for(let c=0;c<current.children.length;c++){
                setChildrenDisable(current.children, current.children[c].id);
              }
            }
          }else{
            // 不是要找的节点，就到子节点里再找找看
            if(Tool.isNotEmpty(current.children)){
              setChildrenDisable(current.children, id);
            }
          }

        }
      };
      /**
       * 编辑
       */
      const treeSelectData = ref();
      const edit = (record: any) => {
        modalVisible.value = true;
        doc.value = Tool.copy(record);
        // 每次进入编辑，都将父分类中的当前文档及其子文档设置为不可选
        treeSelectData.value = Tool.copy(level1.value);
        setChildrenDisable(treeSelectData.value, record.id);
        treeSelectData.value.unshift({id: 0, name:'无'});
      };

      /**
       * 新增
       */
      const add = () => {
        modalVisible.value = true;
        modalLoading.value = false;
        doc.value = {
          ebookId: route.query.ebookId,
        };
        treeSelectData.value = Tool.copy(level1.value);
        treeSelectData.value.unshift({id: 0, name:'无'});
      };

      /**
       * 删除
       */
      const getDeleteNodes = (treeData: any, id:any, nodes:Array<any>) => {
        for(let i=0;i<treeData.length;i++){
          const current = treeData[i];
          // 如果当前节点就是要找的节点
          if(current.id === id){
            nodes.push(current);

            if(Tool.isNotEmpty(current.children)){
              // 遍历其孩子节点，将当前节点的孩子的disabled全部置为disabled
              for(let c=0;c<current.children.length;c++){
                getDeleteNodes(current.children, current.children[c].id, nodes);
              }
            }
          }else{
            // 不是要找的节点，就到子节点里再找找看
            if(Tool.isNotEmpty(current.children)){
              getDeleteNodes(current.children, id, nodes);
            }
          }

        }
      };
      const del = (deleteIds : string) => {
        axios.delete('/doc/delete/' + deleteIds)
             .then((response) => {
               if(response.data.success){
                 handleQuery();
               }
        });
      };
      const showConfirm = (id: string) => {
        let deleteNodes: Array<any> = [];
        getDeleteNodes(level1.value, id, deleteNodes);
        let deleteNames : Array<string> = [];
        let deleteIds : Array<string> = [];
        deleteNodes.forEach((item) => {
          deleteNames.push(item.name);
          deleteIds.push(item.id)
        });
        // console.log(deleteNames, deleteIds);
        Modal.confirm({
          title: '重要提醒',
          icon: createVNode(ExclamationCircleOutlined),
          content: '子文档也会一并删除，且不可恢复!'+ '确认要删除【' + deleteNames.join(',') + '】?',
          onOk() {
            del(deleteIds.join(','));
          },
          // eslint-disable-next-line @typescript-eslint/no-empty-function
          onCancel() {},
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
        treeSelectData,

        showConfirm,
      };

    }
  });
</script>
