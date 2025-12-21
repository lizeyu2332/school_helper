<template>
  <Layout>
    <div class="help-list">
      <!-- 分类筛选 - 大横幅 -->
      <div class="filter-banner">
        <div class="filter-container">
          <div class="filter-section">
            <div class="filter-title">
              <i class="el-icon-menu"></i>
              <span>求助分类</span>
            </div>
            <el-radio-group v-model="selectedCategory" @change="loadHelps" size="medium" class="category-buttons">
              <el-radio-button label="">
                <i class="el-icon-s-grid"></i>
                全部
              </el-radio-button>
              <el-radio-button label="拼车服务">
                <i class="el-icon-truck"></i>
                拼车服务
              </el-radio-button>
              <el-radio-button label="快递代取">
                <i class="el-icon-box"></i>
                快递代取
              </el-radio-button>
              <el-radio-button label="求购服务">
                <i class="el-icon-shopping-cart-2"></i>
                求购服务
              </el-radio-button>
            </el-radio-group>
          </div>
          <div class="publish-section" v-if="user">
            <el-button 
              type="success" 
              icon="el-icon-plus" 
              size="medium"
              @click="$router.push('/help/publish')"
              class="publish-btn">
              发布求助
            </el-button>
          </div>
        </div>
      </div>

      <!-- 求助列表 -->
      <div class="helps-container">
        <el-card v-for="help in helps" :key="help.id" class="help-card" shadow="hover">
          <div class="help-item">
            <div class="help-header-info">
              <h3 class="help-title">{{ help.title }}</h3>
              <div class="help-tags">
                <el-tag type="info">{{ help.category }}</el-tag>
                <el-tag :type="help.status === 0 ? 'success' : 'info'" style="margin-left: 10px;">
                  {{ help.status === 0 ? '进行中' : help.status === 1 ? '已完成' : '已取消' }}
                </el-tag>
              </div>
            </div>
            <p class="description">{{ help.description }}</p>
            <div class="help-location" v-if="help.longitude && help.latitude">
              <el-tag type="success" size="small">
                <i class="el-icon-location"></i> {{ help.locationAddress || '未知地址' }}
              </el-tag>
              <el-button 
                size="mini" 
                type="text" 
                @click.stop="showLocationMap(help)"
                style="margin-left: 10px;">
                查看地图
              </el-button>
            </div>
            <div class="help-footer">
              <span class="time">{{ formatTime(help.createTime) }}</span>
              <el-button size="medium" type="primary" icon="el-icon-message" @click="startChat(help)">联系发布者</el-button>
            </div>
          </div>
        </el-card>
        <el-empty v-if="helps.length === 0" description="暂无求助"></el-empty>
      </div>
      
      <!-- 求助位置地图对话框 -->
      <el-dialog title="求助所在地" :visible.sync="showLocationMapDialog" width="80%">
        <div :id="'help-location-map-' + currentHelpId" style="width: 100%; height: 500px;"></div>
      </el-dialog>
    </div>
  </Layout>
</template>

<script>
import Layout from '../components/Layout.vue'

export default {
  components: {
    Layout
  },
  name: 'HelpList',
  data() {
    return {
      helps: [],
      selectedCategory: '',
      user: null,
      showLocationMapDialog: false,
      currentHelpId: null,
      currentHelp: null,
      locationMap: null
    }
  },
  mounted() {
    this.user = JSON.parse(localStorage.getItem('user') || 'null')
    this.loadHelps()
  },
  methods: {
    loadHelps() {
      const params = this.selectedCategory ? { category: this.selectedCategory } : {}
      this.$http.get('/help/list', { params }).then(res => {
        if (res.success) {
          this.helps = res.data
        }
      })
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString('zh-CN')
    },
    startChat(help) {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      if (!user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      if (user.id === help.userId) {
        this.$message.warning('不能联系自己')
        return
      }
      this.$router.push(`/chat/${help.userId}?helpId=${help.id}`)
    },
    showLocationMap(help) {
      this.currentHelp = help
      this.currentHelpId = help.id
      this.showLocationMapDialog = true
      this.$nextTick(() => {
        this.initLocationMap(help)
      })
    },
    initLocationMap(help) {
      if (!help.longitude || !help.latitude) return
      
      // 检查是否已经加载过高德地图
      if (window.AMap) {
        this.createLocationMap(help)
        return
      }

      // 动态加载高德地图 API
      const script = document.createElement('script')
      script.src = `https://webapi.amap.com/maps?v=1.4.15&key=f4bd907075e26632db123ab93c643986&callback=initAMapHelpLocation`
      script.async = true
      script.defer = true
      
      window.initAMapHelpLocation = () => {
        this.createLocationMap(help)
      }
      
      document.head.appendChild(script)
    },
    createLocationMap(help) {
      if (!window.AMap || !help.longitude || !help.latitude) return
      
      const mapId = 'help-location-map-' + help.id
      // 清除旧地图
      if (this.locationMap) {
        this.locationMap.destroy()
        this.locationMap = null
      }
      
      // 创建地图
      this.locationMap = new AMap.Map(mapId, {
        zoom: 16,
        center: [help.longitude, help.latitude],
        mapStyle: 'amap://styles/normal'
      })

      // 添加标记
      const marker = new AMap.Marker({
        position: [help.longitude, help.latitude],
        map: this.locationMap,
        title: '求助所在地'
      })

      // 添加信息窗体
      const infoContent = `
        <div style="padding: 10px;">
          <b>求助所在地</b><br>
          <span style="color: #666; font-size: 12px;">${help.locationAddress || '未知地址'}</span>
        </div>
      `
      const infoWindow = new AMap.InfoWindow({
        content: infoContent
      })
      
      marker.on('click', () => {
        infoWindow.open(this.locationMap, [help.longitude, help.latitude])
      })
      
      // 自动打开信息窗体
      setTimeout(() => {
        infoWindow.open(this.locationMap, [help.longitude, help.latitude])
      }, 500)
    }
  },
  watch: {
    showLocationMapDialog(newVal) {
      if (!newVal && this.locationMap) {
        this.locationMap.destroy()
        this.locationMap = null
        this.currentHelp = null
        this.currentHelpId = null
      }
    }
  }
}
</script>

<style scoped>
.help-list {
  width: 100%;
}

.filter-banner {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 100%;
}

.filter-section {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
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
  border-radius: 4px;
}

.category-buttons .el-radio-button__inner i {
  font-size: 16px;
}

.category-buttons .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background-color: #fff;
  color: #67C23A;
  border-color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
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

.helps-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.help-card {
  cursor: pointer;
  transition: all 0.3s;
}

.help-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.help-item {
  padding: 10px 0;
}

.help-header-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.help-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
  flex: 1;
}

.help-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.description {
  margin: 15px 0;
  color: #666;
  line-height: 1.8;
  font-size: 14px;
}

.help-location {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

.help-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.time {
  color: #999;
  font-size: 13px;
}
</style>

