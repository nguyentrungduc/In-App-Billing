# In-App-Billing
- Để thu phí sử dụng ứng dụng Android qua Google Play, các nhà phát triển có ba cách làm chủ yếu sau: Một là trực tiếp bán phiên bản có phí, bên cạnh việc có hoặc không phân phối một phiên bản miễn phí độc lập, chẳng hạn như Advanced Download Manager phân phối hai phiên bản miễn phí đi kèm quảng cáo, và một phiên bản độc lập với mức phí 59 “cành” cho thị trường Việt Nam. Hai là họ phân phối phiên bản miễn phí với đầy đủ chức năng, và một ứng dụng độc lập đóng vai trò mở khóa các tính năng cao cấp, mà đại diện phổ biến nhất là bộ đôi Nova Launcher và Nova Launcher Prime. Hai cách làm trên tuy đơn giản hơn trong quá trình phân phối, vì chỉ cần phát hành hai ứng dụng riêng biệt là được. Tuy nhiên, đi kèm với đó là việc cần phải xác thực xem có thực sự người dùng đã mua hàng hay không, hay họ chỉ đơn giản là tải và cài đặt tập tin APK từ nguồn cung cấp miễn phí để dùng “chùa”. Đi kèm với đó là bạn cần phải build riêng hai bản với những tính năng khác nhau nếu chọn phân phối theo cách thứ nhất, chẳng hạn với phiên bản miễn phí là một tùy chọn trỏ tới vị trí của phiên bản trả phí trên Google Play, còn trong phiên bản trả phí là hàng loạt những tính năng độc quyền.

- Ngoài ra, chúng ta còn có cách phân phối theo hướng thứ ba, là chỉ phân phối một ứng dụng duy nhất với giá 0 USD, AUD, GBP hay VND, và cung cấp tùy chọn trả phí trực tiếp trong ứng dụng dưới dạng in-app billing. Cách này phổ biến nhất ở các games, mà tiên phong là Plants vs. Zombies 2, cho tải về chơi miễn phí, nhưng đâu “dễ ăn của ngoại” khi họ có cách “hút máu” dữ dội hơn với các vật phẩm trong game, tức in-app items, hoặc chính xác hơn là in-games inventories. Nhưng đối với ứng dụng, thì cách làm này cũng được áp dụng rất rộng rãi, đặc biệt là các ứng dụng cung cấp nội dung với hình thức thuê bao như Netflix. Và đây là hướng tiếp cận của bài viết này.

- Để thực hiện cách thức thu phí này, thì chúng ta có hai cách: Một là sử dụng trực tiếp Google Play Store AIDL. Cách này thì chúng ta cần phải bỏ ra nhiều thao tác hơn, nhưng được cái cũng nhờ đó mà dễ dàng tùy chỉnh, mở rộng các thao tác hơn. Còn cách thứ hai có phần dễ học, dễ làm và dễ ăn hơn là dùng Google Play Billing Support Library. Thực chất, Billing Lib này là những helper classes được viết trong bộ AIDL kia và đóng gói thành thư viện. Nhưng vì đây là một thư viện chính chủ hẳn hoi nên Google đã catch sẵn một số Exceptions nên nhiệm vụ của bạn không quá phức tạp như dùng AIDL. Và bây giờ, chúng ta bắt đầu tiến hành. Lưu ý: Bạn phải phân phối phiên bản đầu tiên có hỗ trợ thanh toán lên Google Play trước thì mới có thể thử nghiệm được. Sau này, cho dù bạn không phát hành phiên bản thử nghiệm mới có versionCode cao hơn tất cả các phiên bản hiện tại trên Play Store thì vẫn có thể thử nghiệm mua hàng được.
## Google Play Billing là một dịch vụ cho phép mua bán nội dung trên Android
## Các loại sản phẩm
- One-time product: Một sản phẩm trong ứng dụng yêu cầu một khoản phí duy nhất, không định kỳ đối với hình thức thanh toán của người dùng. Các cấp trò chơi bổ sung, hộp loot cao cấp và tệp phương tiện là ví dụ về các sản phẩm một lần. Google Play Console gọi các sản phẩm một lần là sản phẩm được quản lý và Google Play Billing Library gọi chúng là "INAPP".
- Rewarded Product: Một sản phẩm trong ứng dụng yêu cầu người dùng xem quảng cáo video. Cuộc sống thêm, tiền tệ trong trò chơi và hoàn thành nhanh chóng các nhiệm vụ đúng thời gian là những ví dụ về các sản phẩm được thưởng. Google Play Console gọi các sản phẩm được thưởng là sản phẩm có thưởng và Google Play Billing Library gọi chúng là "INAPP".
- Subscriptions:  Một sản phẩm trong ứng dụng yêu cầu một khoản phí định kỳ cho hình thức thanh toán của người dùng. Tạp chí trực tuyến và dịch vụ phát nhạc là ví dụ về đăng ký. Google Play Billing Library gọi là "SUBS".
## Purchase token và order ID
- Google Play Billing theo dõi các sản phẩm và giao dịch bằng cách sử dụng Purchase token và order ID

- Purchase token là một chuỗi đại diện cho quyền của người mua đối với sản phẩm trên Google Play. Nó chỉ ra rằng người dùng Google đã trả tiền cho một sản phẩm cụ thể, được đại diện bởi SKU.
- Một order ID là một chuỗi đại diện cho một giao dịch trên Google Play. Chuỗi này được bao gồm trong biên nhận được gửi qua email cho người mua và nhà phát triển bên thứ ba sử dụng ID đơn hàng để quản lý tiền hoàn lại trong phần Quản lý đơn hàng của Google Play Console. Order ID cũng được sử dụng trong báo cáo bán hàng 
- Đối với các sản phẩm một lần (One-time product) và các sản phẩm được thưởng (Rewarded Product), mỗi lần mua sẽ tạo ra một Purchase token mới và order ID mới 

- Đối với Subscriptions, giao dịch mua ban đầu tạo purchase token và order ID. Đối với mỗi kỳ thanh toán liên tục,purchse ID vẫn giữ nguyên và ID đơn hàng mới được cấp. Nâng cấp, hạ cấp và đăng ký lại đều tạo ra các mã thông báo mua hàng mới và ID đơn hàng.
## In-app product congihuaration options
- One-time product và Subscriptions  có một số tùy chọn cấu hình phổ biến trên Google Play Console
1.Title :  Mô tả ngắn về sản phẩm trong ứng dụng ví dụ như mana
2. Description:  Mô tả dài hơn về sản phẩm trong ứng dụng, chẳng hạn như Rương kho báu trong trò chơi đặc biệt chứa nội dung hữu ích cho nhân vật của bạn. Từ trường này có thể được sử dụng trong trang danh sách cửa hàng ứng dụng của bạn để mô tả sản phẩm trong ứng dụng của bạn.
3. ID Product: id của sản phẩm, đc gọi là SKU trong  Google Play Billing Library
4. Price:  Số tiền người dùng sẽ trả cho sản phẩm trong ứng dụng
- Default price cho One-time product phản ánh số tiền (theo loại tiền ưa thích của người dùng đó) mà người dùng sẽ được tính cho sản phẩm. Default price được tính cho khách hàng một lần cho mỗi lần mua sản phẩm một lần.
-  Rewarded Product không có giá hay default price . Cụ thể, giá trị của tùy chọn này không đại diện cho giá trị của quảng cáo mà người dùng xem.
- Giá mặc định cho một Subscriptions là giá mà người dùng sẽ được tính bằng loại tiền ưa thích của người dùng đó sau khi họ bước vào chu kỳ thanh toán thông thường (đăng ký cũng có thể dùng thử miễn phí và giá giới thiệu). 
## Promo code
- Mã khuyến mại là mã mà người dùng sử dụng để nhận sản phẩm một lần miễn phí. Người dùng nhập mã khuyến mãi trong ứng dụng của bạn hoặc trong ứng dụng Cửa hàng Google Play để nhận sản phẩm một lần miễn phí. Sử dụng mã khuyến mãi để xây dựng cơ sở người dùng cho các sản phẩm một lần của bạn.
## Configation subcription product
- Billing period (thời hạn thanh toán): Tần suất mà người dùng bị tính phí trong khi đăng ký của họ đang hoạt động. Bạn có thể chọn giữa các kỳ thanh toán hàng tuần, 1 tháng, 3 tháng, 6 tháng và hàng năm trong Bảng điều khiển Google Play.
Thanh toán tiếp tục vô thời hạn tại khoảng thời gian và giá được chỉ định cho đăng ký. Mỗi lần gia hạn đăng ký, Google Play sẽ tự động tính phí tài khoản người dùng, sau đó thông báo cho người dùng về các khoản phí sau đó bằng email.
- Free trail period:  Một khoảng thời gian mà người dùng có thể truy cập vào một thuê bao mà không được lập hóa đơn. Thời gian dùng thử miễn phí là một cách để lôi kéo người dùng thử đăng ký của bạn trước khi cam kết mua nó.
Bạn có thể đặt thời gian dùng thử miễn phí từ 3 ngày trở lên.
- Introduce type: Giá của thuê bao qua một số thời hạn thanh toán ban đầu, giới thiệu trực tuyến. Giá giới thiệu là một cách để lôi kéo người dùng thử đăng ký của bạn đồng thời đạt được một số doanh thu. Giá giới thiệu phải thấp hơn giá bình thường của subcription

Để chuẩn bị kiểm tra triển khai Thanh toán Google Play của bạn, hãy thực hiện các tác vụ sau:

Xuất bản ứng dụng của bạn lên một bản thử nghiệm đóng hoặc mở trong Google Play . Sau khi bạn xuất bản một ứng dụng lên bản thử nghiệm, có thể mất vài giờ để ứng dụng có sẵn cho người thử nghiệm.
Đảm bảo mỗi người kiểm tra tham gia vào thử nghiệm của ứng dụng của bạn . Trên URL chọn tham gia thử nghiệm của bạn, những người thử nghiệm của bạn sẽ nhận được lời giải thích về ý nghĩa của việc thử nghiệm và liên kết đến chọn tham gia.
Bạn có thể kiểm tra trên mọi thiết bị phần cứng hỗ trợ Android chạy Android 1.6 trở lên. Phiên bản mới nhất của ứng dụng Google Play phải được cài đặt trên thiết bị. Để biết thông tin chung về cách thiết lập thiết bị để sử dụng trong việc phát triển ứng dụng Android, hãy xem Sử dụng thiết bị phần cứng .

Kiểm tra một ứng dụng Google Play Billing
Kiểm tra với các phản ứng tĩnh
Google Play Billing cung cấp kết hợp ID sản phẩm dành riêng và phản hồi tĩnh được liên kết mà bạn có thể sử dụng để kiểm tra việc triển khai Google Play Billing của mình. Những phản hồi này cho phép bạn xác minh rằng ứng dụng của bạn đang xử lý các phản hồi chính của Google Play. Bạn có thể kiểm tra việc triển khai Google Play Billing bằng các phản hồi tĩnh này trước khi liên quan đến người kiểm tra và ngay cả khi ứng dụng chưa được xuất bản.

Để kiểm tra việc triển khai của bạn bằng các phản hồi tĩnh, bạn tạo yêu cầu Thanh toán Google Play bằng một mặt hàng đặc biệt có ID sản phẩm dành riêng. Mỗi ID sản phẩm dành riêng trả về một phản hồi tĩnh cụ thể từ Google Play. Không có tiền được chuyển khi bạn thực hiện các yêu cầu Thanh toán trên Google Play với ID sản phẩm dành riêng. Ngoài ra, bạn không thể chỉ định hình thức thanh toán khi bạn thực hiện yêu cầu thanh toán với ID sản phẩm dành riêng.

Lưu ý: Phản hồi tĩnh không thể được sử dụng để kiểm tra đăng ký.

Bạn không cần liệt kê các sản phẩm dành riêng trong danh sách sản phẩm của ứng dụng. Google Play đã biết về ID sản phẩm dành riêng. Ngoài ra, bạn không cần tải ứng dụng của mình lên Google Play Console để thực hiện các thử nghiệm phản hồi tĩnh với ID sản phẩm dành riêng. Bạn chỉ cần cài đặt ứng dụng của mình trên thiết bị, đăng nhập vào thiết bị và thực hiện các yêu cầu thanh toán bằng ID sản phẩm dành riêng.

Lưu ý: Trước đây bạn có thể kiểm tra một ứng dụng bằng cách tải lên phiên bản "bản nháp" chưa được công bố. Chức năng này không còn được hỗ trợ. Tuy nhiên, bạn có thể kiểm tra ứng dụng của mình bằng các phản hồi tĩnh ngay cả trước khi bạn tải nó lên Google Play Store. Để biết thêm thông tin, xem Kiểm tra với các phản ứng tĩnh .

Có ba ID sản phẩm dành riêng để kiểm tra phản hồi Thanh toán Google Play tĩnh:

android.test.p mua
Khi bạn thực hiện yêu cầu Thanh toán Google Play với ID sản phẩm này, Google Play sẽ phản hồi như thể bạn đã mua thành công một mặt hàng. Phản hồi bao gồm một chuỗi JSON, chứa thông tin mua hàng giả (ví dụ: ID đơn hàng giả).

android.test.celoned
Khi bạn thực hiện yêu cầu Thanh toán Google Play với ID sản phẩm này, Google Play sẽ phản hồi như thể việc mua hàng đã bị hủy. Điều này có thể xảy ra khi gặp lỗi trong quy trình đặt hàng, chẳng hạn như thẻ tín dụng không hợp lệ hoặc khi bạn hủy đơn đặt hàng của người dùng trước khi bị tính phí.

android.test.item_unav Available
Khi bạn thực hiện yêu cầu Thanh toán Google Play với ID sản phẩm này, Google Play sẽ phản hồi như thể mặt hàng được mua không được liệt kê trong danh sách sản phẩm của ứng dụng của bạn.

Để thực hiện yêu cầu Thanh toán Google Play với ID sản phẩm dành riêng, hãy tạo một REQUEST_PURCHASEyêu cầu bình thường , nhưng thay vì sử dụng ID sản phẩm thực từ danh sách sản phẩm của ứng dụng, hãy sử dụng một trong các ID sản phẩm dành riêng.

Để kiểm tra ứng dụng của bạn bằng ID sản phẩm dành riêng, hãy làm theo các bước sau:

Sửa đổi ứng dụng của bạn để ứng dụng sử dụng một trong ba ID sản phẩm dành riêng trong luồng mua hàng. Để biết thông tin về việc sử dụng ID sản phẩm để mua hàng, hãy tham khảo Cho phép mua sản phẩm trong ứng dụng .
Cài đặt ứng dụng của bạn trên thiết bị hỗ trợ Android.
Bạn không thể sử dụng trình giả lập để kiểm tra Google Play Billing; bạn phải cài đặt ứng dụng của mình trên thiết bị để kiểm tra Google Play Billing.

Để tìm hiểu cách cài đặt ứng dụng trên thiết bị, hãy xem Chạy trên thiết bị .

Đăng nhập vào thiết bị của bạn bằng tài khoản nhà phát triển của bạn.
Bạn không cần sử dụng tài khoản kiểm tra nếu bạn chỉ kiểm tra với ID sản phẩm dành riêng.

Xác minh rằng thiết bị của bạn đang chạy phiên bản được hỗ trợ của ứng dụng Google Play hoặc ứng dụng MyApps.
Nếu thiết bị của bạn đang chạy Android 3.0, Google Play Billing yêu cầu phiên bản 5.0.12 (hoặc cao hơn) của ứng dụng MyApps. Nếu thiết bị của bạn đang chạy bất kỳ phiên bản Android nào khác, Google Play Billing yêu cầu phiên bản 2.3.4 (hoặc cao hơn) của ứng dụng Google Play. Để kiểm tra phiên bản của ứng dụng Google Play, hãy khởi chạy ứng dụng, sau đó mở menu Cài đặt và cuộn xuống để xem thông tin phiên bản.

Chạy ứng dụng của bạn và mua hàng bằng ID sản phẩm dành riêng. Mã trong onPurchasesUpdated() shoud của bạn xử lý đúng các phản ứng tĩnh. Để biết thông tin triển khai onPurchasesUpdated(), hãy tham khảo Cho phép mua sản phẩm trong ứng dụng .
Kiểm tra lại với các id sản phẩm dành riêng khác.
Lưu ý : Thực hiện các yêu cầu Thanh toán trên Google Play bằng ID sản phẩm dành riêng sẽ ghi đè hệ thống sản xuất Google Play thông thường. Khi bạn gửi yêu cầu Thanh toán Google Play cho ID sản phẩm dành riêng, chất lượng dịch vụ sẽ không thể so sánh với môi trường sản xuất.

Kiểm tra luồng mua hoàn chỉnh
Sau khi bạn hoàn thành kiểm tra phản hồi tĩnh và bạn xác minh rằng xác minh chữ ký đang hoạt động trong ứng dụng của mình, bạn có thể kiểm tra triển khai Google Play Billing bằng cách mua hàng trong ứng dụng thực tế. Thử nghiệm mua hàng trong ứng dụng thực tế cho phép bạn kiểm tra trải nghiệm Thanh toán Google Play từ đầu đến cuối, bao gồm các giao dịch mua thực tế từ Google Play và luồng thanh toán thực tế mà người dùng sẽ trải nghiệm trong ứng dụng của bạn.

Lưu ý: Bạn có thể thực hiện kiểm tra từ đầu đến cuối của ứng dụng bằng cách xuất bản nó thành một bản thử nghiệm kín . Điều này cho phép bạn xuất bản ứng dụng lên Google Play Store, nhưng giới hạn tính khả dụng của nó đối với những người thử nghiệm mà bạn chỉ định.

Để kiểm tra việc triển khai Google Play Billing với mua hàng trong ứng dụng thực tế, bạn phải sử dụng tài khoản thử nghiệm. Theo mặc định, tài khoản thử nghiệm duy nhất được đăng ký là tài khoản được liên kết với tài khoản nhà phát triển của bạn. Bạn có thể đăng ký tài khoản kiểm tra bổ sung bằng cách sử dụng Bảng điều khiển Google Play. Nếu bạn chưa thiết lập tài khoản kiểm tra trước đó, hãy xem Thiết lập tài khoản kiểm tra .

Tài khoản thử nghiệm có thể mua một mặt hàng trong danh sách sản phẩm của bạn chỉ khi mặt hàng đó được xuất bản.

Để kiểm tra triển khai Hóa đơn Google Play của bạn với các giao dịch mua thực tế, hãy làm theo các bước sau:

Tải ứng dụng của bạn lên bản nhạc thử nghiệm đã đóng trong Bảng điều khiển Google Play.
Lưu ý: Sau khi tải lên ứng dụng ban đầu, người kiểm tra giấy phép có thể mua hàng từ các phiên bản phát triển của ứng dụng mà không cần tải lên Google Play Console. Điều này cho phép bạn sử dụng các bản dựng đã ký gỡ lỗi và thực hiện các thay đổi mà không phải tải lên phiên bản mới mỗi lần.

Lưu ý: Trước đây bạn có thể kiểm tra một ứng dụng bằng cách tải lên phiên bản "bản nháp" chưa được công bố. Chức năng này không còn được hỗ trợ. Thay vào đó, bạn phải xuất bản ứng dụng của mình lên bản thử nghiệm đã đóng hoặc mở. Để biết thêm thông tin, hãy xem Dự thảo ứng dụng không còn được hỗ trợ .

Tạo các sản phẩm trong ứng dụng của bạn trong Bảng điều khiển Google Play. Để biết thêm chi tiết, hãy tham khảo Tạo sản phẩm một lần và Tạo đăng ký
Cài đặt ứng dụng của bạn trên thiết bị hỗ trợ Android. Bạn không thể sử dụng trình giả lập để kiểm tra Google Play Billing. Để tìm hiểu cách cài đặt ứng dụng trên thiết bị, hãy xem Chạy ứng dụng của bạn trên thiết bị .
Xác minh rằng thiết bị của bạn đang chạy phiên bản được hỗ trợ của ứng dụng Google Play hoặc ứng dụng MyApps. Nếu thiết bị của bạn đang chạy Android 3.0, Google Play Billing yêu cầu phiên bản 5.0.12 (hoặc cao hơn) của ứng dụng MyApps. Nếu thiết bị của bạn đang chạy bất kỳ phiên bản Android nào khác, Google Play Billing yêu cầu phiên bản 2.3.4 (hoặc cao hơn) của ứng dụng Google Play. Để tìm hiểu cách kiểm tra phiên bản của ứng dụng Google Play, hãy xem Cập nhật Google Play .
Thực hiện mua trong ứng dụng trong ứng dụng của bạn.
Lưu ý: Cách duy nhất để thay đổi tài khoản chính trên thiết bị là thực hiện khôi phục cài đặt gốc, đảm bảo bạn đăng nhập bằng tài khoản chính trước.

Người dùng kiểm tra ứng dụng Google Play Billing
Thiết lập tài khoản kiểm tra
Để thiết lập tài khoản người kiểm tra:

Sử dụng Bảng điều khiển Google Play để tải lên và xuất bản các sản phẩm trong ứng dụng mà bạn muốn người kiểm tra có thể mua. Lưu ý rằng bạn có thể tải lên và xuất bản các mục trong ứng dụng trước khi xuất bản APK.
Sử dụng Bảng điều khiển dành cho nhà phát triển để tạo tài khoản người kiểm tra giấy phép:
Điều hướng đến Cài đặt > Chi tiết tài khoản .
Trong phần Kiểm tra giấy phép, hãy thêm địa chỉ email của người kiểm tra của bạn vào tài khoản Gmail với trường truy cập kiểm tra .
Lưu các thay đổi của bạn. Người kiểm tra có thể bắt đầu mua sản phẩm trong ứng dụng của bạn trong vòng 15 phút.
Lưu ý : Tài khoản kiểm tra phải có trên thiết bị Android của người kiểm tra. Nếu thiết bị có nhiều tài khoản, giao dịch mua sẽ được thực hiện với tài khoản đã tải xuống ứng dụng. Nếu không có tài khoản nào tải xuống ứng dụng, giao dịch mua được thực hiện với tài khoản đầu tiên. Người dùng có thể xác nhận tài khoản đang mua hàng bằng cách mở rộng hộp thoại mua hàng.

Hướng dẫn người kiểm tra thực hiện mua thử nghiệm
Sau khi tài khoản kiểm tra được thiết lập, bạn có thể hướng dẫn người dùng thực hiện mua hàng thử nghiệm. Sau đây là một số chi tiết về quy trình mua thử nghiệm:

Người dùng sẽ sử dụng cùng một luồng mua ứng dụng được sử dụng bởi người dùng thông thường.
Sử dụng nên thực hiện ít nhất hai lần mua, một với hình thức thanh toán "luôn được chấp thuận" và một với hình thức thanh toán "luôn bị từ chối". Các hình thức thanh toán thử nghiệm này cho phép bạn đảm bảo ứng dụng của mình phản ứng đúng khi thanh toán được phê duyệt hoặc từ chối. Hình 1 cho thấy các hình thức thanh toán thử nghiệm này khi chúng xuất hiện trong luồng mua hàng:

Hình 1. Các tùy chọn công cụ kiểm tra phương thức thanh toán cho người dùng kiểm tra giấy phép.
Những hình thức thanh toán này là hai hình thức thanh toán duy nhất có sẵn cho những người thử nghiệm được cấp phép. Khi sử dụng các hình thức thanh toán này, luồng mua hàng sẽ trả về kết quả ngay lập tức.
Thuế không được tính cho mua thử nghiệm.
Người kiểm tra được cấp phép sẽ không bị tính phí khi mua hàng của họ.
Google Play chỉ ra giao dịch mua thử bằng cách hiển thị thông báo trên trung tâm của hộp thoại mua.
Lưu ý: Nếu bạn muốn có thể thực hiện nhiều lần mua thử nghiệm cho cùng một sản phẩm trong ứng dụng, hãy đánh dấu mục đó là tiêu thụ sau mỗi lần mua. Để làm như vậy, gọi . consumeAsync()

Kiểm tra với tài khoản thực tế
Khi bạn chuẩn bị khởi chạy một ứng dụng sử dụng Google Play Billing, bạn có thể sử dụng các tùy chọn phát hành đóng hoặc mở của Google Play để kiểm tra xác thực và tải thử nghiệm khi triển khai trước khi phân phối ứng dụng cho tất cả người dùng của bạn.

Với các nhóm thử nghiệm đóng hoặc mở, người dùng có thể cài đặt ứng dụng của bạn từ Google Play và kiểm tra các sản phẩm trong ứng dụng của bạn. Người dùng có thể thực hiện mua hàng thực sự dẫn đến chi phí thực tế cho tài khoản của họ, sử dụng bất kỳ phương thức thanh toán thông thường nào trong Google Play.

Lưu ý: Nếu bạn bao gồm các tài khoản giấy phép thử nghiệm trong các nhóm phân phối thử nghiệm đóng và mở của mình, những người dùng đó sẽ chỉ có thể thực hiện mua hàng thử nghiệm.

Kiểm tra các tính năng cụ thể của sản phẩm một lần
Thử nghiệm chương trình khuyến mãi trong ứng dụng
Nếu ứng dụng của bạn hỗ trợ các chương trình khuyến mãi trong ứng dụng, hãy kiểm tra các trường hợp sử dụng sau.

Người dùng đổi mã khuyến mãi trong ứng dụng
Nếu người dùng đổi mã khuyến mại trong luồng mua hàng của ứng dụng, như được mô tả trong Tạo yêu cầu Thanh toán trên Google Play , hệ thống sẽ gọi onActivityResult()phương thức hoạt động của bạn để xử lý việc mua hàng. Xác minh rằng onActivityResult()xử lý mua hàng đúng cách, cho dù người dùng thanh toán bằng tiền hoặc mã khuyến mãi.

Người dùng đổi mã khuyến mãi trong Cửa hàng Google Play
Nếu người dùng đổi mã khuyến mại trong Cửa hàng Play, có một số quy trình công việc có thể. Xác nhận từng quy trình công việc.

Ứng dụng chưa được cài đặt
Nếu người dùng đổi mã khuyến mại cho một ứng dụng chưa được cài đặt trên thiết bị, Cửa hàng Play sẽ nhắc người dùng cài đặt ứng dụng. (Nếu ứng dụng được cài đặt nhưng không cập nhật, Cửa hàng Play sẽ nhắc người dùng cập nhật ứng dụng.) Kiểm tra trình tự sau trên thiết bị chưa cài đặt ứng dụng của bạn.

Người dùng đổi mã khuyến mại cho ứng dụng trong Cửa hàng Play. Cửa hàng Play sẽ nhắc người dùng cài đặt ứng dụng của bạn.
Người dùng cài đặt và khởi chạy ứng dụng của bạn. Xác minh rằng khi khởi động, ứng dụng gọi getPurchases() và phát hiện chính xác giao dịch mua mà người dùng đã thực hiện bằng mã khuyến mãi.
Ứng dụng đã được cài đặt nhưng không chạy
Nếu người dùng đổi mã khuyến mại cho một ứng dụng được cài đặt trên thiết bị, Cửa hàng Play sẽ nhắc người dùng chuyển sang ứng dụng. Kiểm tra trình tự sau trên thiết bị đã cài đặt ứng dụng của bạn nhưng không chạy:

- Người dùng đổi mã khuyến mại cho ứng dụng trong Cửa hàng Play. Cửa hàng Play nhắc người dùng chuyển sang ứng dụng của bạn.
- Người dùng khởi chạy ứng dụng của bạn. Xác minh rằng khi khởi động ứng dụng gọi getPurchases() và phát hiện chính xác giao dịch mua mà người dùng đã thực hiện bằng mã khuyến mãi.
- Ứng dụng được cài đặt và chạy 
Nếu người dùng đổi mã khuyến mại cho một ứng dụng hiện đang chạy trên thiết bị, Cửa hàng Play sẽ thông báo cho ứng dụng thông qua PURCHASES_UPDATED ý định. Kiểm tra trình tự sau:

- Người dùng khởi chạy ứng dụng. Xác minh rằng ứng dụng đã đăng ký chính xác để nhận được PURCHASES_UPDATEDý định.
- Người dùng khởi chạy ứng dụng Cửa hàng Play, theo cách thủ công hoặc sử dụng URL được tạo có chứa mã khuyến mãi và đổi lại mã khuyến mãi cho ứng dụng. Cửa hàng Play bắn một PURCHASES_UPDATEDý định. Xác minh rằng BroadcastReceiver.onReceive()cuộc gọi lại ứng dụng của bạn kích hoạt để xử lý ý định.
onReceive() Phương pháp của bạn nên đáp ứng ý định bằng cách gọi getPurchases(). Xác minh rằng ứng dụng của bạn gọi phương thức này và ứng dụng sẽ phát hiện chính xác giao dịch mua mà người dùng đã thực hiện với mã khuyến mãi.
Người dùng chuyển trở lại ứng dụng của bạn. Xác minh rằng người dùng có các mặt hàng đã mua.
Kiểm tra tính năng cụ thể đăng ký
- Luồng mua cho các sản phẩm và đăng ký một lần là tương tự nhau, nhưng đăng ký có các kịch bản bổ sung, chẳng hạn như gia hạn đăng ký thành công hoặc bị từ chối. Để giúp bạn kiểm tra ứng dụng của mình cho cả hai tình huống, bạn có thể sử dụng "Công cụ kiểm tra, luôn chấp thuận" và "Công cụ kiểm tra, luôn từ chối" phương thức thanh toán. Sử dụng các công cụ thanh toán này để kiểm tra các kịch bản ngoài kịch bản đăng ký thành công.




