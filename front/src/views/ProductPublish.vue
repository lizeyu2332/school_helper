<template>
  <Layout>
    <div class="product-publish-wrapper">
      <div class="product-publish">
        <el-card>
          <div slot="header">
            <h2>发布商品</h2>
          </div>
          <el-form :model="productForm" :rules="rules" ref="productForm" label-width="120px" style="max-width: 800px;">
            <el-form-item label="商品标题" prop="title">
              <el-input v-model="productForm.title" placeholder="请输入商品标题"></el-input>
            </el-form-item>
            <el-form-item label="商品分类" prop="category">
              <el-select v-model="productForm.category" placeholder="请选择分类">
                <el-option label="二手书" value="二手书"></el-option>
                <el-option label="生活用品" value="生活用品"></el-option>
                <el-option label="历年卷" value="历年卷"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="商品描述" prop="description">
              <el-input type="textarea" :rows="5" v-model="productForm.description" placeholder="请输入商品详细描述"></el-input>
            </el-form-item>
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="productForm.price" :min="0" :precision="2" placeholder="请输入价格"></el-input-number>
            </el-form-item>
            <el-form-item label="交易方式" prop="tradeType">
              <el-radio-group v-model="productForm.tradeType">
                <el-radio label="自提">自提</el-radio>
                <el-radio label="送达">送达</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="送达地点" v-if="productForm.tradeType === '送达'" prop="deliveryPointId">
              <el-button @click="showMapDialog = true" type="primary">选择送达地点</el-button>
              <div v-if="selectedDeliveryPoint" style="margin-top: 10px;">
                <el-tag>{{ selectedDeliveryPoint.name }} - {{ selectedDeliveryPoint.address }}</el-tag>
              </div>
            </el-form-item>
            <el-form-item label="商品所在地">
              <el-button @click="showLocationDialog = true" type="primary">在地图上选择位置</el-button>
              <div v-if="selectedLocation" style="margin-top: 10px;">
                <div style="margin-bottom: 5px;">
                  <el-tag type="success" size="medium" style="font-size: 14px;">
                    <i class="el-icon-location"></i> {{ selectedLocation.displayAddress || selectedLocation.address }}
                  </el-tag>
                </div>
                <div style="color: #666; font-size: 12px; line-height: 1.5;">
                  <div v-if="selectedLocation.detailAddress && selectedLocation.detailAddress !== selectedLocation.displayAddress">
                    <span style="color: #999;">详细地址：</span>{{ selectedLocation.detailAddress }}
                  </div>
                  <div style="margin-top: 3px;">
                    <span style="color: #999;">坐标：</span>
                    ({{ selectedLocation.longitude.toFixed(6) }}, {{ selectedLocation.latitude.toFixed(6) }})
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="商品图片">
              <el-upload
                action="/api/file/upload"
                list-type="picture-card"
                :file-list="fileList"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                :limit="5">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">发布</el-button>
              <el-button @click="$router.back()">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      
      <!-- 地图选点对话框 -->
      <el-dialog title="选择送达地点" :visible.sync="showMapDialog" width="80%" :before-close="handleMapClose">
        <MapPicker @select="handleMapSelect" :delivery-points="deliveryPoints"></MapPicker>
      </el-dialog>
      
      <!-- 商品位置选择对话框 -->
      <el-dialog title="选择商品所在地" :visible.sync="showLocationDialog" width="80%" :before-close="handleLocationClose">
        <MapClickPicker @select="handleLocationSelect"></MapClickPicker>
      </el-dialog>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'
import MapPicker from '../components/MapPicker.vue'
import MapClickPicker from '../components/MapClickPicker.vue'

export default {
  components: {
    Layout,
    MapPicker,
    MapClickPicker
  },
  name: 'ProductPublish',
  data() {
    return {
      productForm: {
        title: '',
        category: '',
        description: '',
        price: 0,
        tradeType: '自提',
        deliveryPointId: null
      },
      rules: {
        title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
        tradeType: [{ required: true, message: '请选择交易方式', trigger: 'change' }]
      },
      fileList: [],
      showMapDialog: false,
      selectedDeliveryPoint: null,
      deliveryPoints: [],
      showLocationDialog: false,
      selectedLocation: null
    }
  },
  mounted() {
    this.loadDeliveryPoints()
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (!user) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
    }
  },
  methods: {
    loadDeliveryPoints() {
      this.$http.get('/delivery/list').then(res => {
        if (res.success) {
          this.deliveryPoints = res.data
        }
      })
    },
    handleMapSelect(point) {
      this.selectedDeliveryPoint = point
      this.productForm.deliveryPointId = point.id
      this.showMapDialog = false
    },
    handleMapClose() {
      this.showMapDialog = false
    },
    handleLocationSelect(location) {
      this.selectedLocation = location
      this.productForm.longitude = location.longitude
      this.productForm.latitude = location.latitude
      // 优先使用友好显示地址，如果没有则使用完整地址
      this.productForm.locationAddress = location.displayAddress || location.address || ''
      this.showLocationDialog = false
    },
    handleLocationClose() {
      this.showLocationDialog = false
    },
    handlePreview(file) {
      // 预览图片
      window.open(file.url || file.response?.url, '_blank')
    },
    handleRemove(file, fileList) {
      this.fileList = fileList
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    },
    handleUploadSuccess(response, file, fileList) {
      if (response.success) {
        // 确保保存服务器返回的真实URL，而不是blob URL
        // Element UI 可能会生成 blob URL 用于预览，我们需要强制使用服务器返回的URL
        let serverUrl = response.url
        // 如果是 /uploads/ 开头的路径，在开发环境中使用后端地址用于预览
        if (serverUrl.startsWith('/uploads/') && process.env.NODE_ENV === 'development') {
          serverUrl = 'http://47.108.59.70:8090' + serverUrl
        }
        this.fileList = fileList.map(f => {
          if (f.uid === file.uid) {
            return {
              ...f,
              url: serverUrl, // 使用服务器返回的URL（开发环境已转换为完整URL）
              response: response // 保存完整的响应，包含原始服务器URL
            }
          }
          return f
        })
        this.$message.success('图片上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
        // 移除失败的文件
        this.fileList = fileList.filter(f => f.uid !== file.uid)
      }
    },
    handleUploadError(err, file, fileList) {
      this.$message.error('图片上传失败：' + (err.message || '网络错误'))
      // 移除失败的文件
      this.fileList = fileList.filter(f => f.uid !== file.uid)
    },
    handleSubmit() {
      this.$refs.productForm.validate((valid) => {
        if (valid) {
          if (this.productForm.tradeType === '送达' && !this.productForm.deliveryPointId) {
            this.$message.warning('请选择送达地点')
            return
          }
          const user = JSON.parse(localStorage.getItem('user') || 'null')
          // 提取所有已上传图片的URL
          // 优先使用 response.url（服务器返回的真实URL），避免使用 blob URL 和完整URL
          const imageUrls = this.fileList
            .map(f => {
              // 优先使用服务器返回的原始URL（相对路径），用于保存到数据库
              const serverUrl = f.response?.url
              if (serverUrl && !serverUrl.startsWith('blob:') && !serverUrl.startsWith('http://')) {
                return serverUrl
              }
              // 如果 response.url 是完整URL，提取相对路径部分
              if (serverUrl && serverUrl.startsWith('http://47.108.59.70:8090/uploads/')) {
                return serverUrl.replace('http://47.108.59.70:8090', '')
              }
              // 如果 file.url 是完整URL，提取相对路径部分
              const fileUrl = f.url
              if (fileUrl && fileUrl.startsWith('http://47.108.59.70:8090/uploads/')) {
                return fileUrl.replace('http://47.108.59.70:8090', '')
              }
              // 如果 file.url 是相对路径且不是 blob URL
              if (fileUrl && !fileUrl.startsWith('blob:') && !fileUrl.startsWith('http://')) {
                return fileUrl
              }
              return ''
            })
            .filter(url => url && url.trim() !== '')
          
          if (imageUrls.length === 0) {
            this.$message.warning('请至少上传一张商品图片')
            return
          }
          
          const formData = {
            ...this.productForm,
            userId: user.id,
            images: JSON.stringify(imageUrls),
            longitude: this.productForm.longitude || null,
            latitude: this.productForm.latitude || null,
            locationAddress: this.productForm.locationAddress || null
          }
          this.$http.post('/product/add', formData).then(res => {
            if (res.success) {
              this.$message.success('发布成功')
              this.$router.push('/')
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.product-publish-wrapper {
  width: 100%;
}

.product-publish {
  width: 100%;
}
</style>
