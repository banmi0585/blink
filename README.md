# Blink Mod 眨眼模组

为 Minecraft 添加逼真的眨眼和闭眼效果。

## 功能

- 自动眨眼，间隔可以调整
- 按 C 键手动眨眼
- 长按 C 键闭眼，松开后睁开
- 闭眼时末影人不会生气
- 专注药水可以禁用自动眨眼
- 眨眼间隔和眼皮占比可以配置

## 安装

把模组 JAR 文件放进 mods 文件夹即可。如果你是多人服务器玩家，末影人无视闭眼功能需要服务端也安装本模组。

## 配置

配置文件在 config/blink.tmol

```toml
blinkIntervalMin = 5      最短眨眼间隔
blinkIntervalMax = 10     最长眨眼间隔
upperLidPercent = 0.5     上眼皮占比
