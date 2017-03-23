require ('com.lucrecapital.mfeed.listener.MFeedListenerController')
require ('com.lucrecapital.tester.Utils')

--listener = mlist.create("239.1.1.9", "30009")

pl_counter = 0;

function pl_handler(id, snapshot)
    pl_counter = pl_counter + 1
end

function pl_handler(id, snapshot)
    print("got snapshot from ".. id.. " currency:"..  snapshot.symbol)
   -- print("Symbol: ".. snapshot.symbol)
   -- print("Exchange: ".. snapshot.exchange)
   -- print("SellBook: ")
   -- for i,level in ipairs(snapshot.sellbook) do
   --     print(level.size.. "@".. level.price)
   -- end
    
   -- print("BuyBook: ")
   -- for i,level in ipairs(snapshot.buybook) do
   --     print(level.size.. "@".. level.price)
   -- end
end    

--mlist.set_pl_handler(listener, pl_handler)
--mlist.stop(listener)
