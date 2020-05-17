(function () {

    var chat = {
        messageToSend: '',
        messageResponses: ['你为何如此优秀呢'],
        init: function () {
            this.cacheDOM();
            this.bindEvents();
        },
        cacheDOM: function () {
            this.$chatHistory = $('.chat-history');
            this.$button = $('button');
            this.$textarea = $('#message-to-send');
            this.$chatHistoryList = this.$chatHistory.find('ul');
        },
        bindEvents: function () {
            this.$button.on('click', this.addMessage.bind(this));
            this.$textarea.on('keyup', this.addMessageEnter.bind(this));
        },
        addMessage: function () {
            this.messageToSend = this.$textarea.val()
            let data = {}
            data.key = '6fe71118e268b6a214b41c7d4bc2e0e4'
            data.question = this.messageToSend
            $.ajax({
                type: 'get',
                contentType: 'application/json;charset=utf-8',
                url: 'http://api.tianapi.com/txapi/robot/index',
                data:data,
                async:false,
                success: (res) => {
                    this.messageResponses = res.newslist[0]["reply"]
                    console.log(this.messageResponses)
                },
                error: (res) => {
                    console.log(res)
                }
            })
            console.log(this.messageResponses)
            this.render();
        },
        render: function () {
            console.log(this.messageResponses)
            this.scrollToBottom();
            if (this.messageToSend.trim() !== '') {
                var template = Handlebars.compile($("#message-template").html());
                var context = {messageOutput: this.messageToSend, time: this.getCurrentTime()};
                this.$chatHistoryList.append(template(context));
                this.scrollToBottom();
                this.$textarea.val('');
                var templateResponse = Handlebars.compile($("#message-response-template").html());
                var contextResponse = {

                    response: this.messageResponses,
                    time: this.getCurrentTime()
                };
                setTimeout(function () {
                    this.$chatHistoryList.append(templateResponse(contextResponse));
                    this.scrollToBottom();
                }.bind(this), 1500);
            }
        },

        addMessageEnter: function (event) {
            if (event.keyCode === 13) {
                this.addMessage();
            }
        },
        scrollToBottom: function () {
            this.$chatHistory.scrollTop(this.$chatHistory[0].scrollHeight);
        },
        getCurrentTime: function () {
            return new Date().toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
        },
        getRandomItem: function (arr) {
            return arr[Math.floor(Math.random() * arr.length)];
        }
    };
    chat.init();

})();