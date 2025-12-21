<template>
  <Layout>
    <div class="product-list-page">
      <div class="page-header">
        <div>
          <p class="page-kicker">精选好物</p>
          <h2>商品列表</h2>
        </div>
        <el-button v-if="user" type="primary" icon="el-icon-plus" @click="$router.push('/product/publish')">发布商品</el-button>
      </div>

      <div class="filter-banner">
        <div class="filter-container">
          <div class="filter-section">
            <div class="filter-title">
              <i class="el-icon-menu"></i>
              <span>分类筛选</span>
            </div>
            <el-radio-group v-model="selectedCategory" @change="loadProducts" size="medium" class="category-buttons">
              <el-radio-button label="">
                <i class="el-icon-s-grid"></i>
                全部
              </el-radio-button>
              <el-radio-button label="二手书">
                <i class="el-icon-reading"></i>
                二手书
              </el-radio-button>
              <el-radio-button label="生活用品">
                <i class="el-icon-shopping-bag-1"></i>
                生活用品
              </el-radio-button>
              <el-radio-button label="历年卷">
                <i class="el-icon-document"></i>
                历年卷
              </el-radio-button>
            </el-radio-group>
          </div>
          <div class="filter-search">
            <el-input
              v-model="searchText"
              placeholder="搜索商品标题或描述"
              clearable
              @keyup.enter.native="loadProducts">
              <el-button slot="append" icon="el-icon-search" @click="loadProducts"></el-button>
            </el-input>
          </div>
        </div>
      </div>

      <div class="products-grid">
        <el-card 
          v-for="product in products" 
          :key="product.id" 
          class="product-card"
          @click.native="$router.push(`/product/${product.id}`)"
          shadow="hover">
          <div class="product-image">
            <img v-if="getFirstImage(product.images)" :src="getFirstImage(product.images)" alt="商品图片">
            <div v-else class="no-image">
              <i class="el-icon-picture"></i>
              <p>暂无图片</p>
            </div>
          </div>
          <div class="product-info">
            <div class="product-meta-line">
              <el-tag size="mini" type="info">{{ product.category }}</el-tag>
              <el-tag size="mini" :type="product.tradeType === '自提' ? 'success' : 'warning'">
                {{ product.tradeType }}
              </el-tag>
            </div>
            <h3 class="product-title">{{ product.title }}</h3>
            <div class="price-row">
              <span class="price">¥{{ product.price }}</span>
              <span class="light-text">点击查看详情</span>
            </div>
          </div>
        </el-card>
      </div>
      <el-empty v-if="products.length === 0" description="暂无商品"></el-empty>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'

export default {
  components: {
    Layout
  },
  name: 'ProductList',
  data() {
    return {
      products: [],
      selectedCategory: '',
      user: null,
      searchText: ''
    }
  },
  mounted() {
    this.user = JSON.parse(localStorage.getItem('user') || 'null')
    this.loadProducts()
  },
  methods: {
    loadProducts() {
      const params = {}
      if (this.selectedCategory) {
        params.category = this.selectedCategory
      }
      if (this.searchText && this.searchText.trim()) {
        params.keyword = this.searchText.trim()
      }
      this.$http.get('/product/list', { params }).then(res => {
        if (res.success) {
          this.products = res.data
        }
      })
    },
    getFirstImage(images) {
      if (!images) return null
      try {
        const imgArray = JSON.parse(images)
        if (imgArray.length > 0) {
          let imgUrl = imgArray[0]
          // 确保URL是字符串且不为空
          if (!imgUrl || typeof imgUrl !== 'string') {
            return null
          }
          // 如果URL是相对路径，确保以/开头
          if (!imgUrl.startsWith('http') && !imgUrl.startsWith('/')) {
            imgUrl = '/' + imgUrl
          }
          // 如果是 /uploads/ 开头的路径，在开发环境中使用后端地址
          if (imgUrl.startsWith('/uploads/') && process.env.NODE_ENV === 'development') {
            imgUrl = 'http://47.108.59.70:8090' + imgUrl
          }
          return imgUrl
        }
        return null
      } catch {
        return null
      }
    }
  }
}
</script>

<style scoped>
.product-list-page {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-kicker {
  color: #5b8cff;
  font-weight: 600;
  margin-bottom: 4px;
  letter-spacing: 0.3px;
}

.page-header h2 {
  margin: 0;
  color: #2b3a67;
}

.filter-banner {
  background: linear-gradient(135deg, #5b8cff 0%, #7c89ff 100%);
  border-radius: 12px;
  padding: 22px 26px;
  margin-bottom: 24px;
  box-shadow: 0 12px 32px rgba(0,0,0,0.14);
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  flex: 1.4;
  display: flex;
  align-items: center;
  gap: 18px;
}

.filter-search {
  flex: 1;
  max-width: 320px;
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
}

.category-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.category-buttons .el-radio-button__inner {
  background: rgba(255,255,255,0.12);
  color: #fff;
  border-radius: 10px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 22px;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 14px;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 32px rgba(0,0,0,0.16);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #f5f7fb 0%, #eef2ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  text-align: center;
  color: #999;
}

.no-image i {
  font-size: 42px;
  display: block;
  margin-bottom: 8px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-meta-line {
  display: flex;
  gap: 10px;
  align-items: center;
}

.product-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #2b3a67;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.light-text {
  color: #9da7b8;
  font-size: 13px;
}
</style>

