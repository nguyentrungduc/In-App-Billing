# In-App-Billing
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
