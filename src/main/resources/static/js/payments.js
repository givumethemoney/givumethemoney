// js 파일로 분리한 후 결제 위젯이 나타나지 않는 이유는
// 비동기적 실행으로 인해 main함수가 먼저 실행됐기 때문
// tosspayments의 위젯을 로딩하는 스크립트가 먼저 실행되어야 한다!
document.addEventListener("DOMContentLoaded", function() {
    if (typeof TossPayments !== 'undefined') {
        main(); // TossPayments가 로드된 경우에만 main() 호출
    } else {
        console.error('TossPayments is not loaded.');
    }
});
// main();
console.log(TossPayments);


async function main() {
    const button = document.getElementById("payment-button");
    // ------  결제위젯 초기화 ------
    const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const tossPayments = TossPayments(clientKey);
    // 회원 결제
    const customerKey = "rvu9KeS25NEP7gWvx6d9N";
    const widgets = tossPayments.widgets({
        customerKey,
    });
    // 비회원 결제
    // const widgets = tossPayments.widgets({ customerKey: TossPayments.ANONYMOUS });

    //------ 주문의 결제 금액 설정 ------
    await widgets.setAmount({
        currency: "KRW",
        value: "5000",
    });

    await Promise.all([
        // ------  결제 UI 렌더링 ------
        widgets.renderPaymentMethods({
        selector: "#payment-method",
        variantKey: "DEFAULT",
        }),
        // ------  이용약관 UI 렌더링 ------
        widgets.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
    ]);

    // ------ '결제하기' 버튼 누르면 결제창 띄우기 ------
    button.addEventListener("click", async function () {
        await widgets.requestPayment({
        orderId: "test123",
        orderName: "cookie",
        successUrl: window.location.origin + "/success",
        failUrl: window.location.origin + "/fail.html",
        customerEmail: "customer123@gmail.com",
        customerName: "김토스",
        customerMobilePhone: "01012341234",
        });
    });

}

