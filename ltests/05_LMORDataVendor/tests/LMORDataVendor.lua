
require('com.lucrecapital.order.FIXOrderSessions')
require('com.lucrecapital.order.FIXConstants')
require('com.lucrecapital.mfeed.generator.MFeedController')
require('com.lucrecapital.mfeed.listener.MFeedListenerController')
require('com.lucrecapital.tester.Utils')
require('com.lucrecapital.tester.fix.Initiators')
require('lutils.utils')
--require('com.lucrecapital.tester.Subscriber')

counter = 0
orders = {}
ss = {}
LMORTime = {}
exReports = {}
provOrders = {}
cliExRep = {}
cliExRepNew = {}

fixGate = {}
fixGateCounter = 0
multicast = {}
multicastCounter = 0

packet_counter = 0

ordersCounter = 0
exReportsCounter = 0
provOrdersCounter = 0
cliExRepCounter = 0
cliExRepNewCounter = 0
-- to do list
-- прогрев нескольких МОРов на выбор (по МОР айди), получаем посредством Хорнета
-- построение графика по среднему аппроксимированному значению, выборка 500 шт из 20к ордеров, аппроксимация по пиковым значениям
-- отдельное логирование ордеров с нестадартным поведением и уведомлением в отдельный топик
-- корректная обработка выполнения ордеров - отслеживание зависших, не выполнившихся или перевыполнившися ордеров, с их последующим логгированием
-- уведомление МОР об окончании прогрева
-- 
function table.removekey(table, key)
    local element = table[key]
    table[key] = nil
    return element
end


--
function pl_handler(id, snapshot)
--    if snapshot.symbol == "USD/CAD" then
--            ss = snapshot
--            ss.exchange = "T1"
--            ss.symbol = "USD/CAD"
--            ss.timestamp = utils.fulltime()
--            mgen.send(mfeed, ss)
--           -- utils.sleep(100)
--            readyToSend = true
--    end
    
    mgen.send(mfeed, ss)
    --packet_counter = packet_counter + 1
--    table.removekey(multicast, snapshot.symbol)
--    multicast[snapshot.symbol] = {}
--    multicast[snapshot.symbol] = snapshot
    
end

function pl_handler2(id, snapshot)
    table.removekey(multicast, snapshot.symbol)
    multicast[snapshot.symbol] = {}
    multicast[snapshot.symbol] = snapshot
end
--
function acceptorReceiveMessage (id, sessionId, message)
    if message.msgType == FIX.MsgType().ORDER_SINGLE then
        objListener : getCurrentTimestamp()
        exRep = {}
        exRep = buildExecutionReport(message, utils.time(), message.price, message.size)
        orderSession.send(acc, exRep) 
        exReports[message.clientID] = exRep
        exReports[message.clientID].timer = utils.nanotime()/1000000
        provOrders[message.clientID] = {}
        provOrders[message.clientID] = message
        provOrders[message.clientID].timer = utils.nanotime()/1000000
        provOrdersCounter = provOrdersCounter + 1
        exReportsCounter = exReportsCounter + 1
    end
end
---
function initiatorReceiveMessage (id, sessionId, message)
    if message.msgType == FIX.MsgType().EXECUTION_REPORT and message.ordStatus == FIX.OrdStatus().FILLED then
        objListener : getCurrentTimestamp()
        cliExRep[message.clOrdID] = {}
        cliExRep[message.clOrdID] = message
        cliExRep[message.clOrdID].timer = utils.nanotime()/1000000
        cliExRepCounter = cliExRepCounter + 1
    elseif message.msgType == FIX.MsgType().EXECUTION_REPORT and message.ordStatus == FIX.OrdStatus().NEW then
        objListener : getCurrentTimestamp()
        cliExRepNew[message.clOrdID] = {}
        cliExRepNew[message.clOrdID] = message
        cliExRepNew[message.clOrdID].timer = utils.nanotime()/1000000
        cliExRepNewCounter = cliExRepNewCounter + 1
    end
end

function initiatorReceiveMessage2 (id, sessionId, message)
    if message.msgType == FIX.MsgType().MARKET_DATA_SNAPSHOT_FULL_REFRESH then
        table.removekey(fixGate, message.symbol)
        fixGate[message.symbol] = {}
        fixGate[message.symbol] = message
        print(message.snapshotNum)
--        --for i = 1, message.noMDEntries do
--        for i = 1, message.noMDEntries do
--            print((message[i].MDEntryType - 48) .. " - " .. message[i].MDEntryPx .. " - " .. message[i].MDEntrySize .. " - " .. message[i].MDEntryPositionNo .. " - " .. message[i].MDEntryID .. " - " .. message[i].MDEntryOriginator)
--        end
        
    end
end
---
function compare(a, b)
    return a.timer < b.timer    
end
---
sessionid = ""

function fix_snap_handler(ide, sid, message)
    if sessionid == "" then
        sessionid = sid
    elseif sessionid == sid then
        print (ide, sid, table_to_string(message))
    end
end
---
function multicastReceiveMessage (id, message)
    printTable("multicast "..id.." message receive", "book", message)
end
---
-- @return
-- 
objSubscriber = luajava.newInstance("com.lucrecapital.tester.Subscriber");
objSubscriber : createSubscriber();

objListener = luajava.newInstance("com.lucrecapital.tester.TextListener");
objSubscriber : setListener(objListener);

--initiator = orderSession.createInitiator("lconfig/config.cfg", initiatorReceiveMessage)
initiator2 = orderSession.createInitiator("lconfig/fixGateConfig.cfg", initiatorReceiveMessage2)
mfeed = mgen.create("239.1.1.240", "30240");
listener = mlist.create("239.1.1.9" , "30009");
listener2 = mlist.create("239.1.1.9" , "30009");
mlist.set_pl_handler(listener, pl_handler)
mlist.set_pl_handler(listener2, pl_handler2)
mlist.start(listener)
mlist.start(listener2)
--orderSession.start(initiator)
--orderSession.start(initiator2)
ss.sellbook = {}
ss.buybook = {}    

N = 5

for i = 0, N do
    level = {}
    level.size = 10000000
    level.price = 0.9 - i / 20000 
    ss.sellbook[i] = level
    
end

for i = 0, N do
    level = {}
    level.size = 1000000
    level.price = 1.1 + i / 40000
    ss.buybook[i] = level
end

ss.exchange = "T1"
ss.symbol = "USD/CAD"

mgen.start(mfeed)
--


flag = true
flag2 = true
readyToSend = true
number = 10000
limit = 20.0
---
fixGate = {}
multicast = {}
---
while true do
--    ss.symbol = objListener : getCurrentTimestamp()
--    mgen.send(mfeed, ss)
      utils.sleep(2000)
      for key, value in pairs(fixGate) do
          fixGateCounter = fixGateCounter + 1
          if value.symbol ~= nil then
            print(value.symbol)
          end
      end
      print(fixGateCounter)
      fixGateCounter = 0
    if  objListener : getLastMessage() : getTitle() == "Begin warming" and flag then
        print("BEGIN WARMING NOW!!!")
        flag = false
        orders = nil
        exReports = nil
        provOrders = nil
        cliExRep = nil
        cliExRepNew = nil
        exReports = {}
        provOrders = {}
        cliExRep = {}
        cliExRepNew = {}
        orders = {}
        
        ordersCounter = 0
        exReportsCounter = 0
        provOrdersCounter = 0
        cliExRepCounter = 0
        cliExRepNewCounter = 0
        
        objListener : setLastMessage(" ")
        -- точка входа для начал прогрева
        acc = orderSession.createAcceptor("lconfig/feedConfig.cfg", acceptorReceiveMessage)
        initiator = orderSession.createInitiator("lconfig/config.cfg", initiatorReceiveMessage)
        orderSession.start(acc)
        orderSession.start(initiator)
        utils.sleep(60000)
        -- прогрев
        counter = 0
        while counter <= (number - 2) do    
            if readyToSend then
                utils.sleep(100)
                counter = counter + 1
                tmpOrd = {}
                tmpOrd = buildMarketOrder("MARKET_"..utils.nanotime(), "USD/CAD", 1000000, "bid", FIX.TimeInForce().DAY)
                --tmpOrd = buildLimitOrder("LIMIT_"..utils.nanotime(), "USD/CAD", 1.5, 1000000, "bid", FIX.TimeInForce().DAY)
                orderSession.send(initiator, tmpOrd)
                objListener : getCurrentTimestamp()
                orders[tmpOrd.clOrdID] = tmpOrd
                orders[tmpOrd.clOrdID].timer = utils.nanotime()/1000000
                orders[tmpOrd.clOrdID].counter = counter

                counter = counter + 1
                ordersCounter = ordersCounter + 1
                tmpOrd2 = {}
                tmpOrd2 = buildMarketOrder("MARKET_"..utils.nanotime(), "USD/CAD", 1000000, "ask", FIX.TimeInForce().DAY)
                orderSession.send(initiator, tmpOrd2)
                objListener : getCurrentTimestamp()
                orders[tmpOrd2.clOrdID] = tmpOrd2
                orders[tmpOrd2.clOrdID].timer = utils.nanotime()/1000000
                orders[tmpOrd2.clOrdID].counter = counter
                ordersCounter = ordersCounter + 1
                --readyToSend = false
            end
        end
        utils.sleep(10000)
        flag = true
        -- точка входа для проверки правильности выполнения ордеров
        print("-------------")
        print("ORDERS: "..ordersCounter)
        print("EXReportsNEW: "..cliExRepNewCounter)
        print("PROVIDEROrders: "..provOrdersCounter)
        print("PROVIDERExReports: "..exReportsCounter)
        print("EXReportsFILLED: "..cliExRepCounter)
        print("-------------")                
        --
        orderSession.stop(initiator)
        orderSession.stop(acc)
        -- точка входа для окончания прогрева и уведомлени МОРа об этом
    elseif objListener : getLastMessage() : getTitle() == "Send last result" and flag2 then
        print("PREPARING AND SENDING RESULTS!!!")
        flag2 = false
        objListener : setLastMessage(" ")
        adress = "/home/koleschenko/visual/gpaph_"..os.date('%Y.%m.%d-%H.%M.%S')..".png"
        -- точка входа для отправки результата
        os.execute("> /home/koleschenko/plotter.txt")
        os.execute("> /home/koleschenko/suspence.txt")
        os.execute("> /home/koleschenko/median.txt")
        os.execute("> /home/koleschenko/medianplotter.txt")
        plotter = io.open("/home/koleschenko/plotter.txt", "a")
        suspence = io.open("/home/koleschenko/suspence.txt", "a")
        median = io.open("/home/koleschenko/median.txt", "a")
        medianPlot = io.open("/home/koleschenko/medianplotter.txt", "a")
        local suspenceCounter = 0
        num = 1
        firstHalf1 = 0
        firstHalf2 = 0
        firstHalf3 = 0
        firstHalf4 = 0
        secondHalf1 = 0
        secondHalf2 = 0
        secondHalf3 = 0
        secondHalf4 = 0
        medPlot1 = 0
        medPlot2 = 0
        medPlot3 = 0
        medPlot4 = 0
        for i = 1, number do
            for key, value in pairs(orders) do
                if value.counter == i then
                    if cliExRep[value.clOrdID] ~= nil then
                        local tmp1 = cliExRepNew[value.clOrdID].timer - value.timer
                        local tmp2 = provOrders[cliExRep[value.clOrdID].orderID].timer - cliExRepNew[value.clOrdID].timer
                        local tmp3 = exReports[cliExRep[value.clOrdID].orderID].timer - provOrders[cliExRep[value.clOrdID].orderID].timer
                        local tmp4 = cliExRep[value.clOrdID].timer - exReports[cliExRep[value.clOrdID].orderID].timer
                        --if tmp1 <= limit and tmp2 <= limit and tmp3 <= limit and tmp4 <= limit then
                        medPlot1 = medPlot1 + tmp1
                        medPlot2 = medPlot2 + tmp2
                        medPlot3 = medPlot3 + tmp3
                        medPlot4 = medPlot4 + tmp4                        
                        if (tmp1 + tmp2 + tmp3 + tmp4) <= limit then
                            if i%25 == 0 then
                                plotter:write(num .. " " .. tmp1 .. " " .. tmp2 .. " ".. tmp3 .." ".. tmp4 .."\n")
                                medianPlot:write(num .. " " .. medPlot1/25 .. " " .. medPlot2/25 .. " ".. medPlot3/25 .." ".. medPlot4/25 .."\n")
                                medPlot1 = 0
                                medPlot2 = 0
                                medPlot3 = 0
                                medPlot4 = 0
                                if i <= number/4 then
                                    firstHalf1 = firstHalf1 + tmp1
                                    firstHalf2 = firstHalf2 + tmp2
                                    firstHalf3 = firstHalf3 + tmp3
                                    firstHalf4 = firstHalf4 + tmp4
                                elseif i >= (number/4)*3 then
                                    secondHalf1 = secondHalf1 + tmp1
                                    secondHalf2 = secondHalf2 + tmp2
                                    secondHalf3 = secondHalf3 + tmp3
                                    secondHalf4 = secondHalf4 + tmp4
                                end
                            end
                        else 
                            suspence:write(num .. " " .. value.clOrdID .." -> ".. cliExRep[value.clOrdID].orderID .."\n") 
                            suspenceCounter = suspenceCounter + 1
                            if i%25 == 0 then
                                plotter:write(num .. " " .. tmp1 .. " " .. tmp2 .. " ".. tmp3 .." ".. tmp4 .."\n")
                                medianPlot:write(num .. " " .. medPlot1/25 .. " " .. medPlot2/25 .. " ".. medPlot3/25 .." ".. medPlot4/25 .."\n")
                                medPlot1 = 0
                                medPlot2 = 0
                                medPlot3 = 0
                                medPlot4 = 0                                
                                if i <= number/4 then
                                    firstHalf1 = firstHalf1 + tmp1
                                    firstHalf2 = firstHalf2 + tmp2
                                    firstHalf3 = firstHalf3 + tmp3
                                    firstHalf4 = firstHalf4 + tmp4
                                elseif i >= (number/4)*3 then
                                    secondHalf1 = secondHalf1 + tmp1
                                    secondHalf2 = secondHalf2 + tmp2
                                    secondHalf3 = secondHalf3 + tmp3
                                    secondHalf4 = secondHalf4 + tmp4
                                end
                            end
                        end
                        num = num + 1
                    end
                end
            end
        end
        median:write("First 100 orders median: Receiving ExRep NEW - ".. firstHalf1/100 .. " Receiving order by Provider - " .. firstHalf2/100 .. " Provider send ExRep FILLED - ".. firstHalf3/100 .. " Sending ExRep to client - " .. firstHalf4/100 .. "\n")
        median:write("Second 100 orders median: Receiving ExRep NEW - ".. secondHalf1/100 .. " Receiving order by Provider - " .. secondHalf2/100 .. " Provider send ExRep FILLED - ".. secondHalf3/100 .. " Sending ExRep to client - " .. secondHalf4/100 .. "\n")
        print("-------------")
        num = 1
        plotter:close()
        suspence:close()
        utils.sleep(1000)
        -- set style histogram rowstacked 
        os.execute("> /home/koleschenko/scen.plt")
        scen = io.open("/home/koleschenko/scen.plt", "a")
        scen:write("set style data histograms\n")
        scen:write("set style histogram rowstacked\n")
        scen:write("set boxwidth 0.6 absolute\n")
        scen:write("set style fill solid 1\n")
        scen:write("set terminal png size 4000, 1080\n")
        scen:write("set output '".. adress .."'\n")
        scen:write("set xrange [-1:400]\n")
        scen:write("set yrange [0: 20]\n")
        scen:write("plot '/home/koleschenko/plotter.txt' u 2 ti 'Receiving ExRep NEW', '' u 3 ti 'Receiving order by Provider', '' u 4 ti 'Provider send ExRep FILLED', '' u 5 ti 'Sending ExRep to client'\n")
        scen:close()
        --
        os.execute("gnuplot /home/koleschenko/scen.plt")
        utils.sleep(10000)
        os.execute("bash /home/koleschenko/mailbash.sh")
        utils.sleep(5000)
        os.execute("rm -rf /home/koleschenko/scen.plt")
        os.execute("rm -rf /home/koleschenko/plotter.txt")
        os.execute("rm -rf /home/koleschenko/suspence.txt")
        os.execute("rm -rf /home/koleschenko/median.txt")
        os.execute('rm -rf '..adress)
        -- удалять график после отправки?
        --orders = nil
        print("RESULTS PREPARED AND SENDED!")
        utils.sleep(5000)
        -- средние значения за каждые 25 ордеров
        adress = "/home/koleschenko/visual/gpaph_"..os.date('%Y.%m.%d-%H.%M.%S').."-MEDIAN.png"
        os.execute("> /home/koleschenko/scen.plt")
        scen = io.open("/home/koleschenko/scen.plt", "a")
        scen:write("set style data histograms\n")
        scen:write("set style histogram rowstacked\n")
        scen:write("set boxwidth 0.6 absolute\n")
        scen:write("set style fill solid 1\n")
        scen:write("set terminal png size 4000, 1080\n")
        scen:write("set output '".. adress .."'\n")
        scen:write("set xrange [-1:400]\n")
        scen:write("set yrange [0: 20]\n")
        scen:write("plot '/home/koleschenko/medianplotter.txt' u 2 ti 'Receiving ExRep NEW', '' u 3 ti 'Receiving order by Provider', '' u 4 ti 'Provider send ExRep FILLED', '' u 5 ti 'Sending ExRep to client'\n")
        scen:close()
        --
        os.execute("gnuplot /home/koleschenko/scen.plt")
        utils.sleep(10000)
        os.execute("bash /home/koleschenko/mailbashMedian.sh")
        utils.sleep(5000)        
        os.execute("rm -rf /home/koleschenko/scen.plt")
        os.execute("rm -rf /home/koleschenko/medianplotter.txt")
        os.execute('rm -rf '..adress)
        
        flag2 = true
        ordersCounter = 0
        exReportsCounter = 0
        provOrdersCounter = 0
        cliExRepCounter = 0
        cliExRepNewCounter = 0
        firstHalf1 = 0
        firstHalf2 = 0
        firstHalf3 = 0
        firstHalf4 = 0
        secondHalf1 = 0
        secondHalf2 = 0
        secondHalf3 = 0
        secondHalf4 = 0
        medPlot1 = 0
        medPlot2 = 0
        medPlot3 = 0
        medPlot4 = 0
        
    elseif objListener : getLastMessage() : getTitle() == "FixGateAnalizer" then
        objListener : setLastMessage(" ")
        orderSession.start(initiator2)
        
        
        
    elseif objListener : getLastMessage() : getTitle() == "Stop connection" then
        objListener : setLastMessage(" ")
        orderSession.stop(initiator)
        orderSession.stop(acc)
        print("CONNECTION STOP!")
    elseif objListener : getLastMessage() : getTitle() == "Make connection" then
        objListener : setLastMessage(" ")
        acc = orderSession.createAcceptor("lconfig/feedConfig.cfg", acceptorReceiveMessage)
        initiator = orderSession.createInitiator("lconfig/config.cfg", initiatorReceiveMessage)
        
        orderSession.start(acc)
        utils.sleep(15000)
        orderSession.start(initiator)
        utils.sleep(15000)
        print("CONNECTION ESTABLISH!")
    end
    --utils.sleep(1000)
end

counter = 0
ss = nil



