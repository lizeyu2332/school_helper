<template>
  <Layout>
    <div class="product-detail-page" v-if="product">
      <el-row :gutter="20">
        <!-- 左侧：商品图片 -->
        <el-col :span="12">
          <el-card>
            <el-carousel v-if="productImages.length > 0" height="500px">
              <el-carousel-item v-for="(img, index) in productImages" :key="index">
                <img :src="img" style="width: 100%; height: 100%; object-fit: contain;">
              </el-carousel-item>
            </el-carousel>
            <div v-else class="no-image-large">
              <i class="el-icon-picture"></i>
              <p>暂无图片</p>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：商品信息 -->
        <el-col :span="12">
          <el-card>
            <h1 class="product-title">{{ product.title }}</h1>
            <div class="product-price-large">¥{{ product.price }}</div>
            
            <el-divider></el-divider>

            <div class="info-row">
              <span class="info-label">分类：</span>
              <el-tag>{{ product.category }}</el-tag>
            </div>
            <div class="info-row">
              <span class="info-label">交易方式：</span>
              <el-tag :type="product.tradeType === '自提' ? 'success' : 'warning'">
                {{ product.tradeType }}
              </el-tag>
            </div>
            <div class="info-row" v-if="product.tradeType === '送达' && deliveryPoint">
              <span class="info-label">送达地点：</span>
              <span>{{ deliveryPoint.name }} - {{ deliveryPoint.address }}</span>
            </div>
            <div class="info-row" v-if="product.longitude && product.latitude">
              <span class="info-label">商品所在地：</span>
              <div style="flex: 1;">
                <div style="margin-bottom: 5px;">
                  <el-tag type="success" size="small">
                    <i class="el-icon-location"></i> {{ product.locationAddress || '未知地址' }}
                  </el-tag>
                  <el-button 
                    size="mini" 
                    type="text" 
                    @click="showLocationMap = true"
                    style="margin-left: 10px;">
                    查看地图
                  </el-button>
                </div>
                <div style="color: #999; font-size: 12px;">
                  坐标：({{ product.longitude.toFixed(6) }}, {{ product.latitude.toFixed(6) }})
                </div>
              </div>
            </div>
            <div class="info-row">
              <span class="info-label">状态：</span>
              <el-tag :type="product.status === 0 ? 'success' : 'info'">
                {{ product.status === 0 ? '待售' : product.status === 1 ? '已售' : '已下架' }}
              </el-tag>
            </div>

            <el-divider></el-divider>

            <h3>商品描述</h3>
            <div class="product-description">{{ product.description }}</div>

            <el-divider></el-divider>

            <div class="review-summary">
              <div class="review-summary-left">
                <span class="info-label">综合评分：</span>
                <el-rate v-model="avgScore" disabled show-score text-color="#ff9900" score-template="{value} 分"></el-rate>
              </div>
              <div class="review-summary-right">
                <span>共 {{ reviewCount }} 条评价</span>
              </div>
            </div>

            <div class="review-section">
              <h3>评价列表</h3>
              <el-empty v-if="reviews.length === 0" description="暂时还没有评价"></el-empty>
              <div v-else class="review-list">
                <div v-for="item in reviews" :key="item.id" class="review-item">
                  <div class="review-header">
                    <span class="review-user">{{ item.userName || ('用户' + item.userId) }}</span>
                    <el-rate v-model="item.score" disabled :max="5" class="review-rate-small"></el-rate>
                  </div>
                  <div class="review-content">{{ item.content }}</div>
                  <div class="review-time">{{ formatTime(item.createTime) }}</div>
                </div>
              </div>
            </div>

            <el-divider></el-divider>

            <div v-if="user && !isOwner && canReview" class="my-review">
              <h3>我要评价</h3>
              <el-rate v-model="myReview.score" :max="5"></el-rate>
              <el-input
                type="textarea"
                :rows="3"
                v-model="myReview.content"
                placeholder="说说你对这个商品的看法吧~"
                style="margin-top: 10px;">
              </el-input>
              <el-button
                type="primary"
                size="small"
                style="margin-top: 10px;"
                @click="submitReview">
                提交评价
              </el-button>
            </div>
            <div v-if="user && !isOwner && !canReview && product.status === 0" class="review-tip">
              <el-alert
                title="提示"
                type="info"
                :closable="false"
                show-icon>
                <template slot="default">
                  <span>只有购买并确认收货后才能评价</span>
                </template>
              </el-alert>
            </div>

            <div class="action-buttons">
              <el-button 
                v-if="product.status === 0 && !isOwner"
                type="success" 
                size="large" 
                icon="el-icon-shopping-cart-2"
                @click="buyProduct"
                style="margin-bottom: 10px;">
                立即购买
              </el-button>
              <el-button 
                type="primary" 
                size="large" 
                icon="el-icon-message"
                @click="contactSeller"
                :disabled="product.status !== 0 || isOwner">
                {{ isOwner ? '这是您发布的商品' : product.status === 0 ? '联系卖家' : '商品已售' }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 商品位置地图对话框 -->
      <el-dialog title="商品所在地" :visible.sync="showLocationMap" width="80%">
        <div id="product-location-map" style="width: 100%; height: 500px;"></div>
      </el-dialog>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'

export default {
  name: 'ProductDetail',
  components: {
    Layout
  },
  data() {
    return {
      product: null,
      deliveryPoint: null,
      productImages: [],
      user: null,
      reviews: [],
      avgScore: 0,
      reviewCount: 0,
      canReview: false,
      myReview: {
        score: 0,
        content: ''
      },
      showLocationMap: false,
      locationMap: null
    }
  },
  computed: {
    isOwner() {
      return this.user && this.product && this.user.id === this.product.userId
    }
  },
  watch: {
    showLocationMap(newVal) {
      if (newVal) {
        this.$nextTick(() => {
          this.initLocationMap()
        })
      } else {
        if (this.locationMap) {
          this.locationMap.destroy()
          this.locationMap = null
        }
      }
    }
  },
  mounted() {
    this.user = JSON.parse(localStorage.getItem('user') || 'null')
    this.loadProduct()
  },
  methods: {
    loadProduct() {
      const id = this.$route.params.id
      if (!id || id === 'publish') {
        this.$message.error('商品ID无效')
        this.$router.push('/')
        return
      }
      this.$http.get(`/product/${id}`).then(res => {
        if (res.success) {
          this.product = res.data
          if (this.product.images) {
            try {
              const imgArray = JSON.parse(this.product.images)
              // 处理图片URL，确保可以正确访问
              this.productImages = imgArray
                .filter(img => img && typeof img === 'string' && img.trim() !== '')
                .map(img => {
                  // 如果URL是相对路径，确保以/开头
                  if (!img.startsWith('http') && !img.startsWith('/')) {
                    img = '/' + img
                  }
                  // 如果是 /uploads/ 开头的路径，在开发环境中使用后端地址
                  if (img.startsWith('/uploads/') && process.env.NODE_ENV === 'development') {
                    img = 'http://47.108.59.70:8090' + img
                  }
                  return img
                })
            } catch {
              this.productImages = []
            }
          }
          if (this.product.deliveryPointId) {
            this.loadDeliveryPoint(this.product.deliveryPointId)
          }
          this.loadReviews()
          if (this.user && !this.isOwner) {
            this.checkCanReview()
          }
        }
      })
    },
    loadDeliveryPoint(id) {
      this.$http.get(`/delivery/${id}`).then(res => {
        if (res.success) {
          this.deliveryPoint = res.data
        }
      })
    },
    loadReviews() {
      this.$http.get(`/review/product/${this.product.id}`).then(res => {
        if (res.success && res.data) {
          this.reviews = res.data.list || []
          this.avgScore = res.data.avgScore || 0
          this.reviewCount = res.data.count || 0
        }
      })
    },
    checkCanReview() {
      if (!this.user || !this.product) return
      this.$http.get('/review/can-review', {
        params: {
          userId: this.user.id,
          productId: this.product.id
        }
      }).then(res => {
        if (res.success) {
          this.canReview = res.data || false
        }
      })
    },
    buyProduct() {
      if (!this.user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$prompt('请输入备注（选填）', '确认购买', {
        confirmButtonText: '确认购买',
        cancelButtonText: '取消',
        inputPlaceholder: '给卖家留言...'
      }).then(({ value }) => {
        const order = {
          productId: this.product.id,
          buyerId: this.user.id,
          remark: value || ''
        }
        this.$http.post('/order/create', order).then(res => {
          if (res.success) {
            this.$message.success('订单创建成功，请前往"我的订单"完成付款')
            this.$router.push('/user/center?tab=orders')
          } else {
            this.$message.error(res.message || '购买失败')
          }
        })
      }).catch(() => {})
    },
    submitReview() {
      if (!this.user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      if (!this.myReview.score) {
        this.$message.warning('请先打分')
        return
      }
      const payload = {
        productId: this.product.id,
        userId: this.user.id,
        score: this.myReview.score,
        content: this.myReview.content || ''
      }
      this.$http.post('/review/add', payload).then(res => {
        if (res.success) {
          this.$message.success('评价成功')
          this.loadReviews()
        } else {
          this.$message.error(res.message || '评价失败')
        }
      })
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString('zh-CN')
    },
    contactSeller() {
      if (!this.user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      // 跳转到聊天页面，带上商品ID
      this.$router.push({
        path: `/chat/${this.product.userId}`,
        query: { productId: this.product.id }
      })
    },
    initLocationMap() {
      if (!this.product.longitude || !this.product.latitude) return
      
      // 检查是否已经加载过高德地图
      if (window.AMap) {
        this.createLocationMap()
        return
      }

      // 动态加载高德地图 API
      const script = document.createElement('script')
      script.src = `https://webapi.amap.com/maps?v=1.4.15&key=f4bd907075e26632db123ab93c643986&callback=initAMapLocation`
      script.async = true
      script.defer = true
      
      window.initAMapLocation = () => {
        this.createLocationMap()
      }
      
      document.head.appendChild(script)
    },
    createLocationMap() {
      if (!window.AMap || !this.product.longitude || !this.product.latitude) return
      
      // 创建地图
      this.locationMap = new AMap.Map('product-location-map', {
        zoom: 16,
        center: [this.product.longitude, this.product.latitude],
        mapStyle: 'amap://styles/normal'
      })

      // 添加标记
      const marker = new AMap.Marker({
        position: [this.product.longitude, this.product.latitude],
        map: this.locationMap,
        title: '商品所在地'
      })

      // 添加信息窗体
      const infoContent = `
        <div style="padding: 10px;">
          <b>商品所在地</b><br>
          <span style="color: #666; font-size: 12px;">${this.product.locationAddress || '未知地址'}</span>
        </div>
      `
      const infoWindow = new AMap.InfoWindow({
        content: infoContent
      })
      
      marker.on('click', () => {
        infoWindow.open(this.locationMap, [this.product.longitude, this.product.latitude])
      })
      
      // 自动打开信息窗体
      setTimeout(() => {
        infoWindow.open(this.locationMap, [this.product.longitude, this.product.latitude])
      }, 500)
    }
  }
}
</script>

<style scoped>
.product-detail-page {
  width: 100%;
}

.product-title {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.product-price-large {
  font-size: 36px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 20px;
}

.info-row {
  margin: 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
}

.product-description {
  margin: 20px 0;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.review-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.review-summary-right {
  color: #999;
  font-size: 13px;
}

.review-section {
  margin-top: 10px;
}

.review-list {
  margin-top: 10px;
  max-height: 260px;
  overflow-y: auto;
}

.review-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.review-user {
  font-weight: 600;
  color: #333;
}

.review-content {
  font-size: 14px;
  color: #555;
  margin-bottom: 4px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-rate-small {
  transform: scale(0.8);
}

.my-review {
  margin-top: 10px;
}

.review-tip {
  margin-top: 10px;
}

.action-buttons {
  margin-top: 30px;
}

.action-buttons .el-button {
  width: 100%;
  padding: 15px;
  font-size: 16px;
}

.no-image-large {
  height: 500px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.no-image-large i {
  font-size: 80px;
  margin-bottom: 20px;
}
</style>
