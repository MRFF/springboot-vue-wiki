<template>


  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <a-row :gutter="24">
        <a-col :span="8">
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
                  v-if="level1.length>0"
                  :columns="columns"
                  :row-key="record => record.id"
                  :data-source="level1"
                  :loading="loading"
                  :pagination="false"
                  size="small"
                  :defaultExpandAllRows="true"
          >
            <template #name="{text, record}">
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                        title="删除后不可恢复，确认删除?"
                        ok-text="是"
                        cancel-text="否"
                        @confirm="showConfirm(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>

        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="文档名"/>
            </a-form-item>
            <a-form-item>

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
            <a-form-item>
              <a-input v-model:value="doc.sort" type="textarea" placeholder="排序"/>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

    </a-layout-content>
  </a-layout>



</template>

<script lang="ts">
  import {defineComponent, onMounted, ref, createVNode } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import axios from 'axios';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
  import E from 'wangeditor';

  export default defineComponent({
    name: 'AdminDoc',
    setup: function () {
      const loading = ref(false);
      const route = useRoute();
      const editor = new E('#content');
      editor.config.zIndex = 0;


      const columns = [
        {
          title: '名称',
          dataIndex: 'name',
          slots: {customRender: 'name'},
        },
        // {
        //   title: '父分类',
        //   key: 'parent',
        //   dataIndex: 'parent'
        // },
        // {
        //   title: '排序',
        //   key: 'sort',
        //   dataIndex: 'sort'
        // },

        {
          title: '操作',
          key: 'action',
          slots: {customRender: 'action'}
        }
      ];

      // const searchParams = ref({});
      const level1 = ref();
      level1.value = [];
      const docs = ref([]);
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
                    // 记得给treeSelectData赋值，否则父文档的分类就为空
                    treeSelectData.value = level1.value;
                  } else{
                    message.error("文档信息加载失败！");
                  }
        });
      };


      // 用以存储当前行的数据
      const doc = ref();
      doc.value = {};
      const handleSave = () => {
        doc.value.content = editor.txt.html();
        axios.post('/doc/save', doc.value)
             .then((response) => {
               if(response.data.success){
                 message.success('保存成功！');
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
       * 根据文档id从数据库查询文档内容
       **/
      const getContent = (id:any) => {
        axios.get('/doc/get-content/' + id).then((response) => {
          if(response.data.success){
            editor.txt.html(response.data.content);
            message.success("文档内容加载成功！")
          } else{
            message.error("文档内容加载失败！");
          }
        });
      };

      /**
       * 编辑
       */
      const treeSelectData = ref();
      const edit = (record: any) => {
        doc.value = Tool.copy(record);
        getContent(doc.value.id);
        // 每次进入编辑，都将父分类中的当前文档及其子文档设置为不可选
        treeSelectData.value = Tool.copy(level1.value);
        setChildrenDisable(treeSelectData.value, record.id);
        treeSelectData.value.unshift({id: 0, name:'无'});
      };

      /**
       * 新增
       */
      const add = () => {
        doc.value = {
          ebookId: route.query.ebookId,
        };
        editor.txt.clear();
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
        editor.create();
      });

      return {
        docs,
        level1,
        columns,
        loading,

        edit,
        add,
        del,

        handleSave,
        doc,

        handleQuery,
        treeSelectData,

        showConfirm,
      };

    }
  });
</script>
