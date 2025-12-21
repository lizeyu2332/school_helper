<template>
  <div class="map-picker">
    <!-- 地图容器 -->
    <div id="map-container" style="width: 100%; height: 500px;"></div>
    
    <!-- 地点列表 -->
    <div class="point-list">
      <h3>选择送达地点</h3>
      <el-radio-group v-model="selectedPointId" @change="handlePointChange" style="width: 100%;">
        <div v-for="point in deliveryPoints" :key="point.id" class="point-item">
          <el-radio :label="point.id">
            <div class="point-info">
              <div class="point-name">{{ point.name }}</div>
              <div class="point-address">{{ point.address }}</div>
            </div>
          </el-radio>
        </div>
      </el-radio-group>
      <el-empty v-if="deliveryPoints.length === 0" description="暂无送达地点"></el-empty>
      <el-button type="primary" @click="handleConfirm" style="margin-top: 20px; width: 100%;">确认选择</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MapPicker',
  props: {
    deliveryPoints: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      map: null,
      markers: [],
      defaultMarker: null,
      selectedPointId: null,
      selectedPoint: null,
      amapKey: 'f4bd907075e26632db123ab93c643986'
    }
  },
  mounted() {
    this.initMap()
  },
  watch: {
    deliveryPoints: {
      handler() {
        this.updateMarkers()
      },
      deep: true
    }
  },
  methods: {
    initMap() {
      // 检查是否已经加载过高德地图
      if (window.AMap) {
        this.createMap()
        return
      }

      // 动态加载高德地图 API
      const script = document.createElement('script')
      // 使用高德地图 Web 服务 API（需要申请 key）
      // 申请地址：https://console.amap.com/dev/key/app
      // 个人开发者可以免费申请，每天有免费调用额度
      script.src = `https://webapi.amap.com/maps?v=1.4.15&key=${this.amapKey}&callback=initAMap`
      script.async = true
      script.defer = true
      
      window.initAMap = () => {
        this.createMap()
      }
      
      script.onerror = () => {
        this.$message.error('地图加载失败，请检查 API Key 是否正确')
        console.error('高德地图加载失败，请检查：')
        console.error('1. API Key 是否正确')
        console.error('2. 是否在 https://console.amap.com/dev/key/app 申请了 key')
        console.error('3. 是否设置了正确的域名白名单')
      }
      
      document.head.appendChild(script)
    },
    createMap() {
      if (!window.AMap) {
        console.error('高德地图 API 未加载')
        return
      }

      // 福州大学旗山校区坐标（经度，纬度）
      // 使用更精确的坐标：福州市闽侯县上街镇学园路2号
      const fzuCenter = [119.200000, 26.050000]
      
      // 创建地图
      this.map = new AMap.Map('map-container', {
        zoom: 15,
        center: fzuCenter,
        mapStyle: 'amap://styles/normal' // 标准地图样式
      })

      // 先添加默认标记（使用默认坐标）
      this.addDefaultMarker(fzuCenter)

      // 加载地理编码插件
      AMap.plugin('AMap.Geocoder', () => {
        // 使用地理编码服务获取福州大学旗山校区的准确坐标
        const geocoder = new AMap.Geocoder({
          city: '福州市' // 城市设为福州市
        })

        // 地理编码：根据地址获取坐标
        geocoder.getLocation('福州大学旗山校区', (status, result) => {
          if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
            const location = result.geocodes[0].location
            const accurateCenter = [location.lng, location.lat]
            
            console.log('地理编码成功，福州大学旗山校区坐标:', accurateCenter)
            
            // 更新地图中心
            this.map.setCenter(accurateCenter)
            this.map.setZoom(15)
            
            // 更新标记位置
            if (this.defaultMarker) {
              this.defaultMarker.setPosition(accurateCenter)
            } else {
              this.addDefaultMarker(accurateCenter)
            }
          } else {
            // 如果地理编码失败，使用默认坐标
            console.warn('地理编码失败，使用默认坐标:', fzuCenter)
          }
        })
      })

      this.updateMarkers()

      // 点击地图可以添加新标记（可选功能）
      this.map.on('click', (e) => {
        const { lng, lat } = e.lnglat
        console.log('点击位置:', lng, lat)
        // 可以在这里添加新标记的逻辑
      })
    },
    addDefaultMarker(position) {
      if (this.defaultMarker) {
        this.defaultMarker.setPosition(position)
      } else {
        this.defaultMarker = new AMap.Marker({
          position: position,
          title: '福州大学旗山校区',
          map: this.map
        })

        // 添加信息窗体
        const infoWindow = new AMap.InfoWindow({
          content: '<div style="padding: 10px;"><b>福州大学旗山校区</b><br><span style="color: #666; font-size: 12px;">福州市闽侯县上街镇学园路2号</span></div>'
        })
        
        this.defaultMarker.on('click', () => {
          infoWindow.open(this.map, position)
        })
      }
    },
    updateMarkers() {
      if (!this.map || !window.AMap) return

      // 清除旧标记
      this.map.remove(this.markers)
      this.markers = []

      // 添加送达地点标记
      if (this.deliveryPoints && this.deliveryPoints.length > 0) {
        this.deliveryPoints.forEach(point => {
          if (point.longitude && point.latitude) {
            const marker = new AMap.Marker({
              position: [point.longitude, point.latitude],
              title: point.name,
              map: this.map
            })

            // 创建信息窗体
            const infoContent = `
              <div style="padding: 10px;">
                <b>${point.name}</b><br>
                <span style="color: #666; font-size: 12px;">${point.address}</span>
              </div>
            `
            const infoWindow = new AMap.InfoWindow({
              content: infoContent
            })

            // 点击标记时选中对应地点并显示信息
            marker.on('click', () => {
              this.selectedPointId = point.id
              this.selectedPoint = point
              infoWindow.open(this.map, [point.longitude, point.latitude])
            })

            this.markers.push(marker)
          }
        })

        // 调整地图视野以包含所有标记
        if (this.markers.length > 0) {
          this.map.setFitView(this.markers, false, [50, 50, 50, 50])
        }
      }
    },
    handlePointChange(pointId) {
      this.selectedPoint = this.deliveryPoints.find(p => p.id === pointId)
      if (this.selectedPoint && this.map && this.selectedPoint.longitude && this.selectedPoint.latitude) {
        // 定位到选中的地点
        this.map.setCenter([this.selectedPoint.longitude, this.selectedPoint.latitude])
        this.map.setZoom(16)
        
        // 高亮选中的标记（打开信息窗体）
        this.markers.forEach(marker => {
          const point = this.deliveryPoints.find(p => 
            Math.abs(p.longitude - marker.getPosition().lng) < 0.0001 &&
            Math.abs(p.latitude - marker.getPosition().lat) < 0.0001
          )
          if (point && point.id === pointId) {
            // 触发标记点击事件以显示信息窗体
            marker.emit('click', { target: marker })
          }
        })
      }
    },
    handleConfirm() {
      if (!this.selectedPoint) {
        this.$message.warning('请选择一个送达地点')
        return
      }
      this.$emit('select', this.selectedPoint)
    }
  },
  beforeDestroy() {
    if (this.map) {
      this.map.destroy()
      this.map = null
    }
  }
}
</script>

<style scoped>
.map-picker {
  display: flex;
  gap: 20px;
  min-height: 500px;
}

#map-container {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 8px;
  z-index: 0;
}

.point-list {
  width: 350px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  max-height: 500px;
  overflow-y: auto;
}

.point-list h3 {
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
}

.point-item {
  margin: 15px 0;
  padding: 15px;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.point-item:hover {
  background: #f0f7ff;
  border-color: #409EFF;
  transform: translateX(5px);
}

.point-info {
  margin-left: 8px;
}

.point-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.point-address {
  font-size: 13px;
  color: #666;
}

/* 选中状态样式 */
.point-item .el-radio.is-checked .point-name {
  color: #409EFF;
}

.point-item .el-radio.is-checked {
  color: #409EFF;
}
</style>
