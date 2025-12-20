<template>
  <Layout>
    <div class="home-page">
      <div class="hero-card">
        <div class="hero-left">
          <p class="hero-kicker">校园二手 · 求助互助</p>
          <h2 class="hero-title">帮你轻松买卖、发布需求，与同学即时沟通</h2>
          <div class="hero-actions">
            <el-button type="primary" icon="el-icon-shopping-cart-full" size="medium" @click="$router.push('/')">逛一逛</el-button>
            <el-button type="primary" plain icon="el-icon-upload" size="medium" v-if="user" @click="$router.push('/product/publish')">发布商品</el-button>
            <el-button type="success" plain icon="el-icon-plus" size="medium" v-if="user" @click="$router.push('/help/publish')">发布求助</el-button>
          </div>
        </div>
        <div class="hero-right">
          <div class="hero-stat">
            <span class="stat-label">分类</span>
            <span class="stat-value">6</span>
          </div>
          <div class="hero-stat">
            <span class="stat-label">实时聊天</span>
            <span class="stat-value">支持</span>
          </div>
          <div class="hero-stat">
            <span class="stat-label">图片上传</span>
            <span class="stat-value">可预览</span>
          </div>
        </div>
      </div>

      <!-- 分类筛选 - 大横幅 -->
      <div class="filter-banner">
        <div class="filter-container">
          <div class="filter-section">
            <div class="filter-title">
              <i class="el-icon-menu"></i>
              <span>商品分类</span>
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
          <div class="search-section">
            <el-input
              v-model="searchText"
              placeholder="搜索商品标题或描述"
              clearable
              @keyup.enter.native="loadProducts">
              <el-button slot="append" icon="el-icon-search" @click="loadProducts"></el-button>
            </el-input>
          </div>
          <div class="publish-section" v-if="user">
            <el-button 
              type="primary" 
              icon="el-icon-plus" 
              size="medium"
              @click="$router.push('/product/publish')"
              class="publish-btn">
              发布商品
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
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
            <h3 class="product-title">{{ product.title }}</h3>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-meta">
              <el-tag size="mini" type="info">{{ product.category }}</el-tag>
              <el-tag size="mini" :type="product.tradeType === '自提' ? 'success' : 'warning'">
                {{ product.tradeType }}
              </el-tag>
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
  name: 'Home',
  components: {
    Layout
  },
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
.home-page {
  width: 100%;
}

.hero-card {
  background: linear-gradient(135deg, #eff3ff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  box-shadow: 0 12px 36px rgba(0,0,0,0.08);
}

.hero-left {
  max-width: 60%;
}

.hero-kicker {
  color: #5b8cff;
  font-weight: 600;
  margin-bottom: 6px;
  letter-spacing: 0.5px;
}

.hero-title {
  font-size: 26px;
  line-height: 1.5;
  color: #2b3a67;
  margin-bottom: 14px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-right {
  display: grid;
  grid-template-columns: repeat(3, auto);
  gap: 12px;
}

.hero-stat {
  background: #fff;
  padding: 14px 16px;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgba(0,0,0,0.06);
  min-width: 110px;
}

.stat-label {
  display: block;
  font-size: 13px;
  color: #7f8c99;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #2b3a67;
}

.filter-banner {
  background: linear-gradient(135deg, #5b8cff 0%, #7c89ff 100%);
  border-radius: 12px;
  padding: 26px 30px;
  margin-bottom: 30px;
  box-shadow: 0 12px 32px rgba(0,0,0,0.14);
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 100%;
}

.filter-section {
  flex: 1.4;
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-section {
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

.filter-title i {
  font-size: 20px;
}

.category-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.category-buttons .el-radio-button {
  margin: 0;
}

.category-buttons .el-radio-button__inner {
  padding: 12px 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  border-radius: 10px;
  border: none;
  background: rgba(255,255,255,0.12);
  color: #fff;
}

.category-buttons .el-radio-button__inner i {
  font-size: 16px;
}

.publish-section {
  margin-left: 20px;
}

.publish-btn {
  padding: 12px 24px;
  font-size: 16px;
  height: auto;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
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
  font-size: 48px;
  margin-bottom: 10px;
}

.product-info {
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.product-price {
  font-size: 22px;
  font-weight: bold;
  color: #f56c6c;
}

.product-meta {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>
