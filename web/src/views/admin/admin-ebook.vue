<template>


  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <div class="about">
        <h1>电子书管理</h1>
      </div>

      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="ebooks"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <!--自定义渲染封面列，用来展示显示图片-->
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>

        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>

        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <router-link :to="'/admin/doc?ebookId=' + record.id">
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
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
          title="电子书表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <!-- 模态框内的表单，用以编辑 -->
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader
                v-model:value="categoryIds"
                :field-names="{ label: 'name', value: 'id', children: 'children' }"
                :options="level1"
        />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script>
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'AdminEbook',
    setup: function () {
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '封面',
          dataIndex: 'cover',
          slots: {customRender: 'cover'}
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类一',
          key: 'category1Id',
          dataIndex: 'category1Id'
        },
        {
          title: '分类二',
          key: 'category2Id',
          dataIndex: 'category2Id'
        },
        {
          title: '文档数',
          dataIndex: 'docCount'
        },
        {
          title: '阅读数',
          dataIndex: 'viewCount'
        },
        {
          title: '点赞数',
          dataIndex: 'voteCount'
        },
        {
          title: 'Action',
          key: 'action',
          slots: {customRender: 'action'}
        }
      ];

      const handleQuery = (params) => {
        loading.value = true;
        // GET请求需要传入params参数，POST请求则无此限制
        axios.get('/ebook/get', {
          params: {
            page: params.page,
            size: params.size
          }
        }).then((response) => {
                  loading.value = false;
                  const data = response.data;
                  ebooks.value = data.content.records;
                  // 重置分页按钮
                  pagination.value.current = params.page;
                  pagination.value.total = data.content.total;
                });
      };

      const handleTableChange = (pagination) => {
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      // 编辑对话框用到的变量
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 用以存储当前行的数据
      const ebook = ref({});
      const handleModalOk = () => {
        modalLoading.value = true;
        setTimeout(() => {
          modalVisible.value = false;
          modalLoading.value = false;
        }, 2000);
      };

      /**
       * 点击编辑
       */
      const edit = (record) => {
        modalVisible.value = true;
        ebook.value = record;
      };

      onMounted(() => {
        // 页面刚加载，应该查询第一页的数据
        handleQuery({
          page: 1,
          size: pagination.value.pageSize
        });
      });

      return {
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,

        modalVisible,
        modalLoading,
        handleModalOk,
        edit,
        ebook,
      };

    }
  });
</script>
