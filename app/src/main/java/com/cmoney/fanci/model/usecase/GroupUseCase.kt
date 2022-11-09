package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.model.GroupModel

class GroupUseCase {

    /**
     * 群組 Mock Data
     */
    suspend fun getGroupMockData(): List<GroupModel> =
        listOf(
            GroupModel(
                groupId = "",
                name = "營養健身葛格Peeta",
                description = "大家好這邊是Peeta葛格\n" +
                        "祝福各位有美好的一天。\n" +
                        "來學習一下正確的運動營養以及健身知識吧，寫些。\n" +
                        "\n" +
                        "健身工作室｜Peeta Fitness：https://www.peeta.tw/fitness/\n" +
                        "美味健康餐｜Peeta TakeTake：https://taketake.peeta.tw/\n" +
                        "超好喝高蛋白｜Peeta Mart：https://mart.peeta.tw/\n" +
                        "-\n" +
                        "品牌/企業 合作請洽漢普頓：hampton.lu0404@gmail.com",
                coverImageUrl = "https://static.pressplay.cc/static/uploads/timeline_cover/20210609/4DF2F857C40E1C1BFFFCA8F381DC8850/60c0d9c1b7ff84DF2F857C40E1C1BFFFCA8F381DC885020210609230953.jpg",
                thumbnailImageUrl = "https://yt3.ggpht.com/ytc/AMLnZu-NRDqGNPQKkLKgnsJlZ3-WGe4gFyn7xcQweyg1=s900-c-k-c0x00ffffff-no-rj",
                categories = listOf(
                    GroupModel.Category(
                        categoryId = "",
                        groupId = "",
                        name = "健身房",
                        channels = listOf(
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "健身葛格Peeta"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "健人蓋伊"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "CYFIT兆佑"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "大H"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "Iron士倫"
                            )
                        )
                    ),
                    GroupModel.Category(
                        categoryId = "",
                        groupId = "",
                        name = "Youtube",
                        channels = listOf(
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "葉式特工 Yes Ranger"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "這群人TGOP"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "Pan Piano"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "阿神"
                            )
                        )
                    )
                )
            ),
            GroupModel(
                groupId = "",
                name = "愛莉莎莎 Alisasa",
                description = "大家好，我是愛莉莎莎Alisasa！\n" +
                        "\n" +
                        "台灣人在韓國留學八個月 \n" +
                        "已經在2018 一月\n" +
                        "回到台灣當全職Youtuber囉！\n" +
                        "\n" +
                        "但是我還是每個月會去韓國\n" +
                        "更新最新的韓國情報 （流行 美妝 美食等等） \n" +
                        "提供給大家不同於一般觀光客\n" +
                        "內行的認識韓國新角度\n" +
                        "\n" +
                        "另外也因為感情經驗豐富（？）\n" +
                        "可以提供給大家一些女生的秘密想法～\n" +
                        "\n" +
                        "希望大家喜歡我的頻道＾＾\n" +
                        "\n" +
                        "\n" +
                        "如果你喜歡我的影片，希望你可以幫我訂閱＋分享\n" +
                        "\n" +
                        "任何合作邀約請洽Pressplay Email :\n" +
                        "alisasa@pressplay.cc\n" +
                        "═════════════════════════════════════\n" +
                        "\n" +
                        "追蹤我 Follow Me \n" +
                        "\n" +
                        "★Facebook社團『愛莉莎莎敗家基地』: https://www.facebook.com/groups/924974291237889/\n" +
                        "★Facebook粉絲專頁: https://www.facebook.com/alisasa11111/\n" +
                        "★Instagram: goodalicia",
                coverImageUrl = "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2021/04/08/realtime/12046522.jpg&s=Y&x=0&y=118&sw=1024&sh=576&sl=W&fw=1050&exp=3600",
                thumbnailImageUrl = "https://img.ltn.com.tw/Upload/ent/page/800/2021/02/15/3440115_1.jpg",
                categories = listOf(
                    GroupModel.Category(
                        categoryId = "",
                        groupId = "",
                        name = "蒼藍鴿",
                        channels = listOf(
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "喝橄欖油排膽結石"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "世紀和解"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "炎上BURN"
                            )
                        )
                    )
                )
            ),
            GroupModel(
                groupId = "",
                name = "韓勾ㄟ金針菇 찐쩐꾸",
                description = "大家好！我是住在台灣的韓勾郎，金針菇\uD83C\uDF44\n" +
                        "曾經在台灣留學, 現在是台灣的新住民！\n" +
                        "非常熱愛台灣，愛吃東西(宵夜)，愛旅遊！\n" +
                        "請多多指教唷唷！\n" +
                        "\n" +
                        "안녕하세요! 대만에 거주중인 한국인 찐쩐꾸입니다\uD83C\uDF44\n" +
                        "대만에서의 생활, 먹방, 여행 관련 컨텐츠를 다루고 있어요\n" +
                        "영상 재밌게 봐주세요!\n" +
                        "\n" +
                        "\uD83C\uDF44金針菇的IG： https://www.instagram.com/ggu__kim\n" +
                        "\uD83C\uDF44金針菇的粉專：https://www.facebook.com/Koreajinzhengu/\n" +
                        "\uD83C\uDF44金針菇的美食社團：https://www.facebook.com/groups/2297603990535075/\n" +
                        "\uD83C\uDF44金針菇的韓國美食品牌：https://www.kimkfoods.com\n" +
                        "\uD83C\uDF44合作邀約請洽PressPlay：jinyoungkim@pressplay.cc",
                coverImageUrl = "https://im.marieclaire.com.tw/s1200c675h100b0/assets/mc/202006/5ED4BCC31193F1591000259.jpeg",
                thumbnailImageUrl = "https://yt3.ggpht.com/ytc/AMLnZu9M4Aaot5_30WfAWiD7VSX4Jv07jDp-h9zwi6veAw=s900-c-k-c0x00ffffff-no-rj",
                categories = listOf(
                    GroupModel.Category(
                        categoryId = "",
                        groupId = "",
                        name = "韓國",
                        channels = listOf(
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "泡菜"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "歐巴"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "整形"
                            )
                        )
                    ),
                    GroupModel.Category(
                        categoryId = "",
                        groupId = "",
                        name = "日本",
                        channels = listOf(
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "和服"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "和牛"
                            ),
                            GroupModel.Channel(
                                channelId = "",
                                creatorId = "",
                                groupId = "",
                                name = "環球影城"
                            )
                        )
                    )
                )
            ),
            GroupModel(
                groupId = "",
                name = "木曜4超玩",
                description = "木曜4超玩！每周四更新！\n" +
                        "努力做＼好＼節目\n" +
                        "\n" +
                        "一日製麵｜木曜4超玩出品\n" +
                        "首推〔東京豚骨拉麵〕\n" +
                        "https://shop.camerabay.tv/products/jitlit-ramen\n" +
                        "\n" +
                        "麥卡貝新節目製作團隊強力募集中！如果你喜歡麥卡貝做節目的態度，\n" +
                        "也具備好節目製作能力與熱情，Eric歡迎你加入我們的行列～～～\n" +
                        "麥卡貝的104：http://bit.ly/2R6gNkT",
                coverImageUrl = "https://edge.camerabay.tv/image/landscape_ch0000501487841963174_mobileweb.jpg",
                thumbnailImageUrl = "https://upload.wikimedia.org/wikipedia/zh/thumb/9/98/%E6%9C%A8%E6%9B%9C4%E8%B6%85%E7%8E%A9_Logo.png/250px-%E6%9C%A8%E6%9B%9C4%E8%B6%85%E7%8E%A9_Logo.png",
                categories = emptyList()
            ),
            GroupModel(
                groupId = "",
                name = "志祺七七 X 圖文不符",
                description = "Hiho 大家好，我是志祺。\n" +
                        "\n" +
                        "你現在收看的是志祺七七，\n" +
                        "365 天不斷更的時事議題評論型頻道！\n" +
                        "在這裡，你會看到⋯⋯\n" +
                        "\n" +
                        "\uD83D\uDCA1\n" +
                        "// 即時議題・堅持每日發片的時事分析 //\n" +
                        "\n" +
                        "遇到各種議題的時候，社會上總是充滿對立。\n" +
                        "常常不同立場之間，只剩下謾罵，無法互相溝通、討論。\n" +
                        "\n" +
                        "志祺七七，剖析不同立場觀點，\n" +
                        "能讓關心社會上重要議題、擔心立場極化的你，\n" +
                        "了解不同立場的不同想法。\n" +
                        "\n" +
                        "\uD83D\uDCA1\n" +
                        "// 多元議題・不同立場都說讚 \uD83D\uDC4D //\n" +
                        "\n" +
                        "人權、同婚、兩岸關係、中美貿易戰、能源議題、政治評論...\n" +
                        "志祺七七不畏懼各種敏感議題，全盤呈現多樣化主題與觀點。\n" +
                        "\n" +
                        "同時，也獲得不同政治立場的一致好評！\n" +
                        "成為台灣唯一訪問過蔡英文、郭台銘、韓國瑜、朱立倫等一線政治人物的新世代時事評論員 ✨\n" +
                        "\n" +
                        "　　＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿\n" +
                        "\uD83C\uDF0D　 探索世界・現在就開啟右上角鈴鐺訂閱志祺七七吧　\uD83C\uDF60\n" +
                        "　　￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣\n" +
                        "\n" +
                        "\uD83E\uDD1D 如果你想與志祺七七合作，歡迎寫信到：hi77@simpleinfo.cc 與我們聯繫",
                coverImageUrl = "https://rcollege.site.nthu.edu.tw/var/file/103/1103/img/3980/903580733.png",
                thumbnailImageUrl = "https://yt3.ggpht.com/ytc/AMLnZu_mEHNg36wJR-TcG0cv3iW90gv2pLbHytyM-te44w=s88-c-k-c0x00ffffff-no-rj",
                categories = emptyList()
            ),
            GroupModel(
                groupId = "",
                name = "Onion Man",
                description = "每週日固定更新，有時不定時更新，不過我想休息就休息\n" +
                        "\n" +
                        "FACEBOOK:https://www.facebook.com/onionman4704/\n" +
                        "IG:https://instagram.com/onionman__/\n" +
                        "\n" +
                        "工作合作請洽：Justin\n" +
                        "Mail : onionmanwork@gmail.com",
                coverImageUrl = "https://cdn.cybassets.com/s/files/15409/ckeditor/pictures/content_db9f4b8c-76a3-4344-9fd8-23b28557fb96.jpg",
                thumbnailImageUrl = "https://yt3.ggpht.com/ytc/AMLnZu86KS86CvxekKUVThxtlnAqfSniEukvq60Z7hsP--g=s88-c-k-c0x00ffffff-no-rj",
                categories = emptyList()
            ),
            GroupModel(
                groupId = "",
                name = "蔡阿嘎Life",
                description = "蔡阿嘎第二個YouTube頻道：分享幕後花絮、生活隨拍小影片！\n" +
                        "(合作洽談聯絡信箱：withgalovetaiwan@gmail.com)",
                coverImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlqUBe7LUe_bz4yX_pDBnUSAtv9rIE5wmtBQ&usqp=CAU",
                thumbnailImageUrl = "https://yt3.ggpht.com/ytc/AMLnZu85jToYmeQY9-itkBeJnH43IY9TWHFRj24zzLlGGA=s88-c-k-c0x00ffffff-no-rj",
                categories = emptyList()
            )
        )

}