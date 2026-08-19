"""
生成步兵实体纹理 (64x64 PNG)，符合 HumanoidModel 的 UV 布局。

HumanoidModel UV 布局 (64x64):
  Head:    x=0-32,  y=0-16  (front=8-16,8-16)
  Body:    x=16-32, y=16-32
  R.Arm:   x=40-48, y=16-32
  L.Arm:   x=32-40, y=48-64
  R.Leg:   x=0-16,  y=16-32
  L.Leg:   x=16-32, y=48-64
"""
import struct, zlib, os

W, H = 64, 64

# 颜色定义
SKIN = (200, 160, 120, 255)       # 皮肤色
HAIR = (80, 55, 35, 255)          # 头发色
UNIFORM = (70, 100, 55, 255)      # 军绿色上衣
PANTS = (45, 55, 35, 255)         # 深绿裤子
BOOT = (50, 40, 30, 255)          # 靴子色
EYE = (40, 40, 40, 255)           # 眼睛
MOUTH = (160, 120, 90, 255)       # 嘴巴

def make_pixel(x, y):
    """根据 UV 坐标返回 RGBA"""
    # ── Head (x:0-31, y:0-15) ──
    if 0 <= x < 32 and 0 <= y < 16:
        # 脸部区域 (front face): x=8-15, y=8-15
        if 8 <= x < 16 and 8 <= y < 16:
            # 眼睛
            if y == 10 and x in (9, 13):
                return EYE
            # 嘴巴
            if y == 13 and 10 <= x < 14:
                return MOUTH
            return SKIN
        # 头发区域 (top of head)
        if 8 <= x < 16 and y < 8:
            return HAIR
        # 头部侧面/后面/顶部/底部
        return HAIR

    # ── Body (x:16-31, y:16-31) ──
    if 16 <= x < 32 and 16 <= y < 32:
        return UNIFORM

    # ── Right Arm (x:40-47, y:16-31) ──
    if 40 <= x < 48 and 16 <= y < 32:
        return UNIFORM

    # ── Right Leg (x:0-15, y:16-31) ──
    if 0 <= x < 16 and 16 <= y < 32:
        if y >= 28:
            return BOOT
        return PANTS

    # ── Left Arm (x:32-39, y:48-63) ──
    if 32 <= x < 40 and 48 <= y < 64:
        return UNIFORM

    # ── Left Leg (x:16-31, y:48-63) ──
    if 16 <= x < 32 and 48 <= y < 64:
        if y >= 60:
            return BOOT
        return PANTS

    # 默认：透明
    return (0, 0, 0, 0)


# 生成像素数据
pixels = bytearray()
for y in range(H):
    for x in range(W):
        r, g, b, a = make_pixel(x, y)
        pixels.extend([r, g, b, a])

raw = bytes(pixels)


def chunk(ctype, data):
    c = ctype + data
    return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xffffffff)


# 构建 IDAT
idat = b''
for y in range(H):
    idat += b'\x00' + raw[y * W * 4:(y + 1) * W * 4]

# 构建 PNG
png = b'\x89PNG\r\n\x1a\n'
png += chunk(b'IHDR', struct.pack('>IIBBBBB', W, H, 8, 6, 0, 0, 0))
png += chunk(b'IDAT', zlib.compress(idat))
png += chunk(b'IEND', b'')

# 写入文件
out_dir = r'e:\McMod\ridingthe-storm-warof-kings2\src\main\resources\assets\riding-the-storm-war-of-kings-2\textures\entity'
os.makedirs(out_dir, exist_ok=True)
out_path = os.path.join(out_dir, 'infantry.png')
with open(out_path, 'wb') as f:
    f.write(png)
print(f'Done: {out_path}')
