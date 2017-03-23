require ('com.lucrecapital.order.FIXConstants')

-----------------------------------------------------------------------------------------------------------------------
function printTable (title, name, table, tab)
  printLog = false
  if not printLog then return; end

  if title ~= nil then io.write("-- ", title, " ---------------------------------------------------\n"); end
  if tab   == nil then tab = ""; end

  io.write(tab, name, ":\n");
  for key, value in pairs(table) do
    itemType = type(value)
    if itemType == "table" then printTable(nil, key, value, tab.."  ");
    else io.write(tab, "  ", itemType, " ", key, " = ", tostring(value), "\t"); end
  end
  if title ~= nil then io.write("\n"); end
end

-----------------------------------------------------------------------------------------------------------------------
function getFixSide (side)
  if type(side) == "boolean" then
    if side then return FIX.Side().BUY
    else         return Fix.Side().SELL; end
  end
  if side == "b" or side == "bid" or side == "buy" or side == FIX.Side().BUY then return FIX.Side().BUY
  else return FIX.Side().SELL; end
end

-----------------------------------------------------------------------------------------------------------------------
function buildBook (exchange, symbol)
  local book    = {}
  book.exchange = exchange
  book.symbol   = symbol
  book.sellbook = {}
  book.buybook  = {}
  return book
end

-----------------------------------------------------------------------------------------------------------------------
function buildPriceLevel (price, size)
  local level = {}
  level.price = price
  level.size  = size
  return level
end

-----------------------------------------------------------------------------------------------------------------------
function buildMarketOrder (id, symbol, size, side, tif)
  local order       = {}
  order.msgType     = FIX.MsgType().ORDER_SINGLE
  order.clOrdID     = id
  order.symbol      = symbol
  order.ordType     = FIX.OrdType().MARKET
  order.size        = size
  order.minQty      = size
  order.side        = getFixSide(side)
  order.timeInForce = tif
  order.timer       = utils.nanotime()
  order.providerOrderTime = 0
  return order
end

-----------------------------------------------------------------------------------------------------------------------
function buildLimitOrder (id, symbol, price, size, side, tif)
  local order       = {}
  order.msgType     = FIX.MsgType().ORDER_SINGLE
  order.clOrdID     = id
  order.symbol      = symbol
  order.ordType     = FIX.OrdType().LIMIT
  order.price       = price
  order.size        = size
  order.minQty      = size
  order.side        = getFixSide(side)
  order.timeInForce = tif
  order.timer       = utils.nanotime()
  order.providerOrderTime = 0
  return order
end

function buildCancel (id, origID, symbol, side, size)
  local order       = {}
  order.msgType     = FIX.MsgType().ORDER_CANCEL_REQUEST
  order.clOrdID     = id
  order.origClOrdID = origID
  order.symbol      = symbol
  order.orderQty    = size
  order.side        = getFixSide(side)
  return order
end

-----------------------------------------------------------------------------------------------------------------------
function buildExecutionReport (order, orderId, price, size)
  local report         = {}
  report.msgType       = FIX.MsgType().EXECUTION_REPORT
  report.execTransType = FIX.ExecTransType().STATUS

  if size <= 0 then
    report.ordStatus   = FIX.OrdStatus().NEW
    report.execType    = FIX.ExecType().NEW
  elseif size < order.size then
    report.ordStatus   = FIX.OrdStatus().PARTIALLY_FILLED
    report.execType    = FIX.ExecType().PARTIAL_FILL
  else
    report.ordStatus   = FIX.OrdStatus().FILLED
    report.execType    = FIX.ExecType().FILL
   end

  report.ordType       = order.ordType
  report.clOrdID       = order.clOrdID
  report.orderID       = order.orderID
  report.execID        = orderId
  report.clientID      = order.clientID
  report.symbol        = order.symbol
  report.price         = price
  report.size          = order.size
  report.side          = getFixSide(order.side)
  report.timeInForce   = order.timeInForce
  report.lastPx        = price
  report.lastQty       = size
  report.cumQty        = size
  report.futSettDate   = os.date("%Y%m%d" , os.time() + 24 * 3600)
  report.securityExchange = "T1"
  
  return report
end

