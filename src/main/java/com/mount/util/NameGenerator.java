package com.mount.util;

import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * 中式名字生成器。
 * 生成 2-3 个字的中文姓名（姓氏 + 1-2 个名字）。
 */
public class NameGenerator {

    private NameGenerator() {}

    /** 姓氏池 */
    private static final List<String> SURNAMES = List.of(
            "张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴",
            "徐", "孙", "马", "胡", "朱", "郭", "何", "罗", "高", "林",
            "梁", "宋", "郑", "谢", "韩", "唐", "冯", "于", "董", "萧",
            "程", "曹", "袁", "邓", "许", "傅", "沈", "曾", "彭", "吕",
            "苏", "卢", "蒋", "蔡", "贾", "丁", "魏", "薛", "叶", "阎",
            "余", "潘", "杜", "戴", "夏", "钟", "汪", "田", "任", "姜",
            "范", "方", "石", "姚", "谭", "廖", "邹", "熊", "金", "陆",
            "郝", "孔", "白", "崔", "康", "毛", "邱", "秦", "江", "史",
            "顾", "侯", "邵", "孟", "龙", "万", "段", "雷", "钱", "汤",
            "尹", "黎", "易", "常", "武", "乔", "贺", "赖", "龚", "文"
    );

    /** 名字用字池（男性化/军事风格） */
    private static final List<String> GIVEN_NAMES = List.of(
            "强", "伟", "勇", "军", "磊", "涛", "杰", "峰", "斌", "刚",
            "毅", "鹏", "飞", "明", "亮", "浩", "宇", "博", "天", "龙",
            "虎", "威", "震", "锐", "锋", "战", "卫", "国", "忠", "义",
            "信", "德", "仁", "智", "礼", "诚", "正", "平", "安", "宁",
            "远", "达", "超", "越", "升", "腾", "翔", "航", "海", "洋",
            "山", "岳", "岩", "石", "松", "柏", "林", "森", "风", "云",
            "雷", "电", "光", "辉", "耀", "灿", "炎", "烈", "猛", "悍",
            "钢", "铁", "铜", "金", "银", "玉", "宝", "珍", "贵", "荣",
            "华", "富", "贵", "祥", "瑞", "福", "禄", "寿", "喜", "庆",
            "春", "夏", "秋", "冬", "东", "西", "南", "北", "中", "央"
    );

    /**
     * 生成一个 2-3 字的随机中文姓名。
     * 格式：姓氏 + 1-2 个名字用字（50% 概率 2 字，50% 概率 3 字）。
     *
     * @param random 随机数源
     * @return 生成的中文姓名
     */
    public static String generateName(RandomSource random) {
        String surname = SURNAMES.get(random.nextInt(SURNAMES.size()));
        String givenName = GIVEN_NAMES.get(random.nextInt(GIVEN_NAMES.size()));

        // 50% 概率生成 3 字名
        if (random.nextBoolean()) {
            String givenName2 = GIVEN_NAMES.get(random.nextInt(GIVEN_NAMES.size()));
            // 避免两个字重复
            while (givenName2.equals(givenName)) {
                givenName2 = GIVEN_NAMES.get(random.nextInt(GIVEN_NAMES.size()));
            }
            return surname + givenName + givenName2;
        }

        return surname + givenName;
    }
}
