local super = peripheral.wrap
peripheral.wrap = function(name)
    local wrapped = super(name)

    if peripheral.getType(name) == "staff" then
        print("wrapped staff")
        local super = wrapped.push;
        wrapped.push = function(iota)
            iota.retrieve()
            super()
        end
    end

    return wrapped
end