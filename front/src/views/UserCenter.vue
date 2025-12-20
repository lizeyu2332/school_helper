<template>
  <Layout>
    <div class="user-center">
      <el-card>
        <div slot="header">
          <h2>个人中心</h2>
        </div>
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="我的订单" name="orders">
            <el-tabs type="border-card">
              <el-tab-pane label="我购买的">
                <el-table :data="buyerOrders" style="width: 100%">
                  <el-table-column prop="productTitle" label="商品" width="200">
                    <template slot-scope="scope">
                      <span v-if="scope.row.productTitle">{{ scope.row.productTitle }}</span>
                      <span v-else style="color: #999;" @click="loadProductTitle(scope.row)">
                        <i class="el-icon-loading" v-if="scope.row.loading"></i>
                        <span v-else>加载中...</span>
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="price" label="金额" width="100">
                    <template slot-scope="scope">
                      ¥{{ scope.row.price }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="120">
                    <template slot-scope="scope">
                      <el-tag :type="getOrderStatusType(scope.row.status)">
                        {{ getOrderStatusText(scope.row.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="createTime" label="下单时间" width="180">
                    <template slot-scope="scope">
                      {{ formatTime(scope.row.createTime) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="200">
                    <template slot-scope="scope">
                      <el-button 
                        v-if="scope.row.status === 0" 
                        size="small" 
                        type="success" 
                        @click="payOrder(scope.row.id)">
                        付款
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 2" 
                        size="small" 
                        type="primary" 
                        @click="receiveOrder(scope.row.id)">
                        确认收货
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 0 || scope.row.status === 1" 
                        size="small" 
                        type="danger" 
                        @click="cancelOrder(scope.row.id)">
                        取消订单
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 3" 
                        size="small" 
                        @click="goToProduct(scope.row.productId)">
                        去评价
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="buyerOrders.length === 0" description="暂无购买订单"></el-empty>
              </el-tab-pane>
              <el-tab-pane label="我卖出的">
                <el-table :data="sellerOrders" style="width: 100%">
                  <el-table-column prop="productTitle" label="商品" width="200">
                    <template slot-scope="scope">
                      <span v-if="scope.row.productTitle">{{ scope.row.productTitle }}</span>
                      <span v-else style="color: #999;">商品ID: {{ scope.row.productId }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="price" label="金额" width="100">
                    <template slot-scope="scope">
                      ¥{{ scope.row.price }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="120">
                    <template slot-scope="scope">
                      <el-tag :type="getOrderStatusType(scope.row.status)">
                        {{ getOrderStatusText(scope.row.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="remark" label="买家备注" width="150">
                    <template slot-scope="scope">
                      <span v-if="scope.row.remark" :title="scope.row.remark">
                        {{ scope.row.remark.length > 10 ? scope.row.remark.substring(0, 10) + '...' : scope.row.remark }}
                      </span>
                      <span v-else style="color: #999;">无</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="createTime" label="下单时间" width="180">
                    <template slot-scope="scope">
                      {{ formatTime(scope.row.createTime) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="200">
                    <template slot-scope="scope">
                      <el-button 
                        v-if="scope.row.status === 1" 
                        size="small" 
                        type="primary" 
                        icon="el-icon-truck"
                        @click="shipOrder(scope.row.id)">
                        发货
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 0" 
                        size="small" 
                        type="info" 
                        disabled>
                        等待买家付款
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 2" 
                        size="small" 
                        type="info" 
                        disabled>
                        已发货，等待买家确认
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 3" 
                        size="small" 
                        type="success" 
                        disabled>
                        交易完成
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 4" 
                        size="small" 
                        type="info" 
                        disabled>
                        已取消
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="sellerOrders.length === 0" description="暂无卖出订单"></el-empty>
              </el-tab-pane>
            </el-tabs>
          </el-tab-pane>
          <el-tab-pane label="我的商品" name="products">
            <el-table :data="myProducts" style="width: 100%">
              <el-table-column prop="title" label="标题" width="200"></el-table-column>
              <el-table-column prop="category" label="分类" width="120"></el-table-column>
              <el-table-column prop="price" label="价格" width="100"></el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
                    {{ scope.row.status === 0 ? '待售' : scope.row.status === 1 ? '已售' : '已下架' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button size="small" @click="editProduct(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteProduct(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="我的求助">
            <el-table :data="myHelps" style="width: 100%">
              <el-table-column prop="title" label="标题" width="200"></el-table-column>
              <el-table-column prop="category" label="分类" width="120"></el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
                    {{ scope.row.status === 0 ? '进行中' : scope.row.status === 1 ? '已完成' : '已取消' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button size="small" @click="editHelp(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteHelp(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="我的评价">
            <el-table :data="myReviews" style="width: 100%">
              <el-table-column prop="productTitle" label="商品标题" width="220"></el-table-column>
              <el-table-column prop="score" label="评分" width="120">
                <template slot-scope="scope">
                  <el-rate v-model="scope.row.score" disabled :max="5"></el-rate>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="评价内容"></el-table-column>
              <el-table-column prop="createTime" label="时间" width="180">
                <template slot-scope="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template slot-scope="scope">
                  <el-button size="small" type="danger" @click="deleteReview(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
    
    <!-- 支付弹窗 -->
    <el-dialog
      title="扫码支付"
      :visible.sync="payDialogVisible"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false">
      <div class="pay-dialog-content">
        <div class="pay-qrcode">
          <img :src="payQrcodeUrl" alt="支付码" class="qrcode-img">
        </div>
        <div class="pay-tips">
          <p>请使用微信或支付宝扫描上方二维码完成支付</p>
          <p class="support-text">支持信用卡付款</p>
        </div>
        <div class="pay-actions">
          <el-button type="success" size="medium" @click="confirmPayment" :loading="paying">
            我已支付
          </el-button>
          <el-button size="medium" @click="cancelPayment">取消支付</el-button>
        </div>
      </div>
    </el-dialog>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'

export default {
  components: {
    Layout
  },
  name: 'UserCenter',
  data() {
    return {
      activeTab: 'products',
      myProducts: [],
      myHelps: [],
      myReviews: [],
      buyerOrders: [],
      sellerOrders: [],
      user: null,
      payDialogVisible: false,
      currentOrderId: null,
      paying: false,
      payQrcodeUrl: require('../assets/支付.png')
    }
  },
  mounted() {
    this.user = JSON.parse(localStorage.getItem('user') || 'null')
    if (!this.user) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    // 检查URL参数，切换到指定tab
    const tab = this.$route.query.tab
    if (tab === 'orders') {
      this.activeTab = 'orders'
      this.loadOrders()
    } else {
      this.loadMyProducts(this.user.id)
      this.loadMyHelps(this.user.id)
      this.loadMyReviews(this.user.id)
    }
  },
  watch: {
    '$route'(to) {
      if (to.query.tab === 'orders') {
        this.activeTab = 'orders'
        this.loadOrders()
      }
    }
  },
  methods: {
    loadMyProducts(userId) {
      this.$http.get(`/product/user/${userId}`).then(res => {
        if (res.success) {
          this.myProducts = res.data
        }
      })
    },
    loadMyHelps(userId) {
      this.$http.get(`/help/user/${userId}`).then(res => {
        if (res.success) {
          this.myHelps = res.data
        }
      })
    },
    loadMyReviews(userId) {
      this.$http.get(`/review/user/${userId}`).then(res => {
        if (res.success) {
          this.myReviews = res.data || []
        }
      })
    },
    editProduct(product) {
      // 编辑商品逻辑
      this.$message.info('编辑功能待实现')
    },
    deleteProduct(id) {
      this.$confirm('确定要删除这个商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/product/${id}`).then(res => {
          if (res.success) {
            this.$message.success('删除成功')
            const user = JSON.parse(localStorage.getItem('user') || 'null')
            this.loadMyProducts(user.id)
          }
        })
      })
    },
    editHelp(help) {
      // 编辑求助逻辑
      this.$message.info('编辑功能待实现')
    },
    deleteHelp(id) {
      this.$confirm('确定要删除这个求助吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/help/${id}`).then(res => {
          if (res.success) {
            this.$message.success('删除成功')
            const user = JSON.parse(localStorage.getItem('user') || 'null')
            this.loadMyHelps(user.id)
          }
        })
      })
    },
    deleteReview(id) {
      this.$confirm('确定要删除这个评价吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/review/${id}`).then(res => {
          if (res.success) {
            this.$message.success('删除成功')
            const user = JSON.parse(localStorage.getItem('user') || 'null')
            this.loadMyReviews(user.id)
          }
        })
      })
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString('zh-CN')
    },
    handleTabClick(tab) {
      if (tab.name === 'orders') {
        this.loadOrders()
      } else if (tab.name === 'products') {
        this.loadMyProducts(this.user.id)
      } else if (tab.name === 'helps') {
        this.loadMyHelps(this.user.id)
      } else if (tab.name === 'reviews') {
        this.loadMyReviews(this.user.id)
      }
    },
    loadOrders() {
      if (!this.user) return
      // 加载买家订单
      this.$http.get(`/order/buyer/${this.user.id}`).then(res => {
        if (res.success) {
          this.buyerOrders = res.data || []
          // 如果商品名称为空，尝试加载
          this.buyerOrders.forEach(order => {
            if (!order.productTitle && order.productId) {
              this.loadProductTitle(order)
            }
          })
        }
      })
      // 加载卖家订单
      this.$http.get(`/order/seller/${this.user.id}`).then(res => {
        if (res.success) {
          this.sellerOrders = res.data || []
          // 如果商品名称为空，尝试加载
          this.sellerOrders.forEach(order => {
            if (!order.productTitle && order.productId) {
              this.loadProductTitle(order)
            }
          })
        }
      })
    },
    loadProductTitle(order) {
      if (order.loading) return
      order.loading = true
      this.$http.get(`/product/${order.productId}`).then(res => {
        order.loading = false
        if (res.success && res.data) {
          this.$set(order, 'productTitle', res.data.title)
        }
      }).catch(() => {
        order.loading = false
      })
    },
    payOrder(orderId) {
      this.currentOrderId = orderId
      this.payDialogVisible = true
    },
    confirmPayment() {
      if (!this.currentOrderId) return
      this.paying = true
      this.$http.post(`/order/${this.currentOrderId}/pay`).then(res => {
        this.paying = false
        if (res.success) {
          this.$message.success('付款成功')
          this.payDialogVisible = false
          this.currentOrderId = null
          this.loadOrders()
        } else {
          this.$message.error(res.message || '付款失败')
        }
      }).catch(() => {
        this.paying = false
        this.$message.error('付款失败，请重试')
      })
    },
    cancelPayment() {
      this.payDialogVisible = false
      this.currentOrderId = null
    },
    shipOrder(orderId) {
      this.$confirm('确认发货？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post(`/order/${orderId}/ship`).then(res => {
          if (res.success) {
            this.$message.success('发货成功')
            this.loadOrders()
          } else {
            this.$message.error(res.message || '发货失败')
          }
        })
      })
    },
    receiveOrder(orderId) {
      this.$confirm('确认收货？确认后可以评价商品', '提示', {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post(`/order/${orderId}/receive`).then(res => {
          if (res.success) {
            this.$message.success('确认收货成功，现在可以评价了')
            this.loadOrders()
          } else {
            this.$message.error(res.message || '确认收货失败')
          }
        })
      })
    },
    cancelOrder(orderId) {
      this.$confirm('确定要取消这个订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post(`/order/${orderId}/cancel`).then(res => {
          if (res.success) {
            this.$message.success('订单已取消')
            this.loadOrders()
          } else {
            this.$message.error(res.message || '取消订单失败')
          }
        })
      })
    },
    getOrderStatusText(status) {
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知'
    },
    getOrderStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'info',
        2: 'primary',
        3: 'success',
        4: 'info'
      }
      return typeMap[status] || ''
    },
    goToProduct(productId) {
      this.$router.push(`/product/${productId}`)
    }
  }
}
</script>

<style scoped>
.user-center {
  min-height: 100vh;
}

.pay-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.pay-qrcode {
  margin-bottom: 20px;
}

.qrcode-img {
  width: 280px;
  height: auto;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 10px;
  background: #fff;
}

.pay-tips {
  margin-bottom: 24px;
  color: #606266;
}

.pay-tips p {
  margin: 8px 0;
  font-size: 14px;
}

.support-text {
  color: #67c23a;
  font-weight: 500;
}

.pay-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.el-header {
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.el-main {
  padding: 20px;
  background: #fff;
  margin: 20px;
  border-radius: 4px;
}
</style>

