(function ()
{
    var _0 = 48;
    var _9 = 57;
    var _A = 65;
    var _Z = 90;
    var _a = 97;
    var _z = 122;

    var ZERO = [0];
    var ONE = [1];

    var trim = function (bs)
    {
        var length = bs.length;
        for (var i = 0; i < length; i++)
        {
            if (bs[i])
            {
                if (i == 0)
                    return bs;
                return bs.slice(i);
            }
        }

        if (length == 1)
            return bs;
        else
            return ZERO;
    };

    var isZero = function (bs)
    {
        var n = bs.length;
        for (var i = 0; i < n; i++)
        {
            if (bs[i])
                return false;
        }
        return true;
    };

    var compare = function (bs1, bs2)
    {
        return compare0(bs1, 0, bs1.length, bs2, 0, bs2.length);
    };

    var compare0 = function (bs1, from1, to1, bs2, from2, to2)
    {
        var n = to1 - from1;
        var m = to2 - from2;
        var l = m > n ? m : n;
        for (var i = 0; i < l; i++)
        {
            var b1 = i + n - l < 0 ? 0 : bs1[i + n - l + from1] || 0;
            var b2 = i + m - l < 0 ? 0 : bs2[i + m - l + from2] || 0;

            if (b1 > b2)
                return 1;

            if (b2 > b1)
                return -1;
        }
        return 0;
    };

    var add = function (bs1, bs2)
    {
        var n = bs1.length;
        var m = bs2.length;
        var l = m > n ? m : n;
        var r = new Array(l + 1);

        for (var i = 0; i < l; i++)
        {
            var b1 = i >= n ? 0 : bs1[n - i - 1] || 0;
            var b2 = i >= m ? 0 : bs2[m - i - 1] || 0;

            var k = l - i;
            var x = b1 + b2;

            while (x > 0)
            {
                x += r[k] || 0;
                r[k] = x % 256;
                x = Math.floor(x / 256);
                k--;
            }
        }
        return trim(r);
    };

    var subtract0 = function (bs1, from1, to1, bs2, from2, to2)
    {
        var n = to1 - from1;
        var m = to2 - from2;
        var l = m > n ? m : n;

        var b = false;
        for (var i = 0; i < l; i++)
        {
            var k = n - i - 1 + from1;
            var b1 = i >= n ? 0 : bs1[k] || 0;
            var b2 = i >= m ? 0 : bs2[m - i - 1 + from2] || 0;
            if (b)
                b2 += 1;

            if (b1 >= b2)
            {
                bs1[k] = b1 - b2;
                b = false;
            }
            else
            {
                bs1[k] = b1 + 256 - b2;
                b = true;
            }
        }
    };

    var subtract = function (bs1, bs2)
    {
        var n = bs1.length;
        var m = bs2.length;
        var l = m > n ? m : n;

        var r;
        if (l == n)
            r = bs1.slice(0);
        else
            r = new Array(l - n).concat(bs1);
        subtract0(r, 0, n, bs2, 0, m);
        return trim(r);
    };

    var multiply = function (bs1, bs2)
    {
        var n = bs1.length;
        var m = bs2.length;
        var r = new Array(n + m);

        for (var i = n - 1; i >= 0; i--)
        {
            for (var j = m - 1; j >= 0; j--)
            {
                var b1 = bs1[i] || 0;
                var b2 = bs2[j] || 0;

                var k = i + j + 1;
                var x = b1 * b2;

                while (x > 0)
                {
                    x += r[k] || 0;
                    r[k] = x % 256;
                    x = Math.floor(x / 256);
                    k--;
                }
            }
        }

        return trim(r);
    };

    var mod = function (bs1, bs2)
    {
        var c = compare(bs1, bs2);
        if (c < 0)
        {
            return bs1;
        }
        else if (c == 0)
        {
            return ZERO;
        }

        var bs10 = bs1;
        bs1 = trim(bs1);
        bs2 = trim(bs2);

        var n = bs1.length;
        var m = bs2.length;
        if (bs1 == bs10)
            bs1 = bs10.slice(0);

        var l = n - m + 1;
        for (var i = 0; i < l; i++)
        {
            while (compare0(bs1, 0, i + m, bs2, 0, m) >= 0)
            {
                subtract0(bs1, 0, i + m, bs2, 0, m);
            }
        }

        return trim(bs1);
    };

    var divide = function (bs1, bs2)
    {
        var result = {};

        var c = compare(bs1, bs2);
        if (c < 0)
        {
            result.q = ZERO;
            result.r = bs1;

            return result;
        }
        else if (c == 0)
        {
            result.q = ONE;
            result.r = ZERO;

            return result;
        }

        var bs10 = bs1;
        bs1 = trim(bs1);
        bs2 = trim(bs2);

        var n = bs1.length;
        var m = bs2.length;
        if (bs1 == bs10)
            bs1 = bs10.slice(0);

        var l = n - m + 1;
        var r = new Array(l);
        for (var i = 0; i < l; i++)
        {
            while (compare0(bs1, 0, i + m, bs2, 0, m) >= 0)
            {
                subtract0(bs1, 0, i + m, bs2, 0, m);

                var x = 1;
                var k = i;
                while (x > 0)
                {
                    x += r[k] || 0;
                    r[k] = x % 256;
                    x = Math.floor(x / 256);
                    k--;
                }
            }
        }

        result.q = trim(r);
        result.r = trim(bs1);

        return result;
    };

    var modPow0 = function (bs, e, m)
    {
        var bs1;
        if ((e[e.length - 1] & 1) == 0)
        {
            if (isZero(e))
                return ONE;

            var b = false;
            for (var i = 0; i < e.length; i++)
            {
                var x = e[i];
                if (b)
                    x += 256;
                b = (x & 1) == 1;
                e[i] = x >> 1;
            }

            bs1 = modPow0(bs, e, m);
            return mod(multiply(bs1, bs1), m);
        }
        else
        {
            e[e.length - 1]--;
            if (isZero(e))
                return bs;

            bs1 = modPow0(bs, e, m);
            return mod(multiply(bs1, bs), m);
        }
    };

    var modPow = function (bs, e, m)
    {
        var e0 = e;
        e = trim(e);

        if (e == e0)
            e = e.slice(0);

        bs = mod(bs, m);

        return modPow0(bs, e, m);
    };

    var toString = function (bs, radix)
    {
        if (radix > 36)
            return;

        var n = bs.length;
        var s = "";

        while (!isZero(bs))
        {
            var r = 0;
            for (var i = 0; i < n; i++)
            {
                var x = bs[i] + r * 256;

                r = x % radix;

                bs[i] = Math.floor(x / radix);
            }

            var c;
            if (r < 10)
                c = _0 + r;
            else
                c = _a + r - 10;

            s = String.fromCharCode(c) + s;
        }

        if (s.length == 0)
            return "0";

        return s;
    };

    var parse = function (s, radix)
    {
        if (radix > 36)
            return;

        var c1 = _a + radix - 11;
        var c2 = _A + radix - 11;

        var n = s.length, i, x;
        var bs0 = new Array(n);
        for (i = 0; i < n; i++)
        {
            var c = s.charCodeAt(i);
            if (c >= _0 && c <= _9)
                x = c - _0;
            else if (c >= _a && c <= c1)
                x = c - _a + 10;
            else if (c >= 65 && c <= c2)
                x = c - _A + 10;
            else
                return;

            bs0[i] = x;
        }

        var bs = new Array(Math.floor(n * Math.log(radix) / Math.log(256)) + 1);

        var t = 0;
        while (!isZero(bs0))
        {
            var r = 0;
            for (i = 0; i < n; i++)
            {
                x = bs0[i] + r * radix;
                r = x % 256;
                bs0[i] = Math.floor(x / 256);
            }

            bs[bs.length - (++t)] = r;
        }

        return trim(bs);
    };

    Cyan.BigInteger = function (bytes, signum)
    {
        if (isZero(bytes))
        {
            this.signum = 0;
            this.bytes = ZERO;
        }
        else
        {
            this.signum = signum < 0 ? -1 : ( signum == 0 ? 0 : 1);
            this.bytes = trim(bytes);
            if (this.bytes == bytes)
                this.bytes = bytes.slice(0);
        }
    };

    Cyan.BigInteger.ZERO = new Cyan.BigInteger(ZERO);
    Cyan.BigInteger.ONE = new Cyan.BigInteger(ONE);

    Cyan.BigInteger.prototype.toString = function (radix)
    {
        if (this.signum == 0)
            return "0";
        else if (this.signum > 0)
            return toString(this.bytes, radix || 10);
        else
            return "-" + toString(this.bytes, radix || 10);
    };

    Cyan.BigInteger.parse = function (s, radix)
    {
        if (s.length == 0)
            return;

        var signum = 1;
        var c = s.charAt(0);
        if (c == '-')
        {
            signum = -1;
            s = s.substring(1);
        }
        else if (c == '+')
        {
            s = s.substring(1);
        }

        var bytes = parse(s, radix || 10);
        return new Cyan.BigInteger(bytes, signum);
    };

    Cyan.BigInteger.prototype.negate = function ()
    {
        return new Cyan.BigInteger(this.bytes, -this.signum);
    };

    Cyan.BigInteger.prototype.add = function (val)
    {
        if (this.signum == 0)
            return val;

        if (val.signum == 0)
            return this;

        if (this.signum == val.signum)
            return new Cyan.BigInteger(add(this.bytes, val.bytes), this.signum);

        var bs1 = this.bytes;
        var bs2 = val.bytes;
        var signum = this.signum;

        var c = compare(bs1, bs2);
        if (c == 0)
            return Cyan.BigInteger.ZERO;

        if (c == -1)
        {
            bs1 = val.bytes;
            bs2 = this.bytes;
            signum = val.signum;
        }

        return new Cyan.BigInteger(subtract(bs1, bs2), signum);
    };

    Cyan.BigInteger.prototype.subtract = function (val)
    {
        if (this.signum == 0)
            return val.negate();

        if (val.signum == 0)
            return this;

        if (this.signum != val.signum)
            return new Cyan.BigInteger(add(this.bytes, val.bytes), this.signum);

        var bs1 = this.bytes;
        var bs2 = val.bytes;
        var signum = this.signum;

        var c = compare(bs1, bs2);
        if (c == 0)
            return Cyan.BigInteger.ZERO;

        if (c == -1)
        {
            bs1 = val.bytes;
            bs2 = this.bytes;
            signum = -signum;
        }

        return new Cyan.BigInteger(subtract(bs1, bs2), signum);
    };

    Cyan.BigInteger.prototype.multiply = function (val)
    {
        if (this.signum == 0 || val.signum == 0)
            return Cyan.BigInteger.ZERO;

        return new Cyan.BigInteger(multiply(this.bytes, val.bytes), this.signum == val.signum ? 1 : -1);
    };

    Cyan.BigInteger.prototype.mod = function (val)
    {
        if (val.signum == 0)
            return;
        if (this.signum == 0)
            return Cyan.BigInteger.ZERO;

        return new Cyan.BigInteger(mod(this.bytes, val.bytes), this.signum);
    };

    Cyan.BigInteger.prototype.divide = function (val)
    {
        if (val.signum == 0)
            return;

        if (this.signum == 0)
            return Cyan.BigInteger.ZERO;

        var r = divide(this.bytes, val.bytes);
        return new Cyan.BigInteger(r.q, this.signum == val.signum ? 1 : -1);
    };

    Cyan.BigInteger.prototype.divideAndMod = function (val)
    {
        if (val.signum == 0)
            return;

        if (this.signum == 0)
        {
            return {
                q: Cyan.BigInteger.ZERO,
                r: Cyan.BigInteger.ZERO
            }
        }

        var r = divide(this.bytes, val.bytes);
        return {
            q: new Cyan.BigInteger(r.q, this.signum == val.signum ? 1 : -1),
            r: new Cyan.BigInteger(r.r, this.signum)
        }
    };

    Cyan.BigInteger.prototype.modPow = function (e, m)
    {
        if (m.signum == 0)
            return;

        if (this.signum == 0)
        {
            if (e.signum == 0)
                return;
            else
                return Cyan.BigInteger.ZERO;
        }

        if (e.signum == 0)
            return Cyan.BigInteger.ONE;

        if (e.signum < 0)
            return;

        return new Cyan.BigInteger(modPow(this.bytes, e.bytes, m.bytes), this.signum);
    };

    Cyan.Base64 = function (c1, c2, c3)
    {
        this.c1 = (c1 || '+').charCodeAt(0);
        this.c2 = (c2 || '/').charCodeAt(0);
        this.c3 = (c3 || '=').charCodeAt(0);
    };

    Cyan.Base64.prototype.byteToBase64 = function (b)
    {
        if (b >= 0)
        {
            if (b < 26)
                return _A + b;
            else if (b < 52)
                return _a + b - 26;
            else if (b < 62)
                return _0 + b - 52;
            else if (b == 62)
                return this.c1;
            else if (b == 63)
                return this.c2;
        }

        throw new Error(b + " is not a base64 byte");
    };

    Cyan.Base64.prototype.base64ToByte = function (c)
    {
        if (c >= _A && c <= _Z)
            return c - _A;
        else if (c >= _a && c <= _z)
            return c - _a + 26;
        else if (c >= _0 && c <= _9)
            return c - _0 + 52;
        else if (c == this.c1)
            return 62;
        else if (c == this.c2)
            return 63;

        throw new Error(c + " is not a base64 char");
    };

    Cyan.Base64.prototype.byteArrayToBase64 = function (bs)
    {
        var chars = new Array(Math.floor(bs.length * 4 / 3) + 1);

        var n = Math.floor(bs.length / 3) * 3;
        var b1, b2, b3, index = 0;
        for (var i = 0; i < n; i += 3)
        {
            b1 = bs[i] & 0xff;
            b2 = bs[i + 1] & 0xff;
            b3 = bs[i + 2] & 0xff;

            chars[index++] = this.byteToBase64(b1 >> 2);
            chars[index++] = this.byteToBase64(((b1 & 0x03) << 4) + (b2 >> 4));
            chars[index++] = this.byteToBase64(((b2 & 0x0f) << 2) + (b3 >> 6));
            chars[index++] = this.byteToBase64(b3 & 0x3f);
        }

        var d = bs.length - n;
        if (d == 1)
        {
            b1 = bs[n] & 0xff;

            chars[index++] = this.byteToBase64(b1 >> 2);
            chars[index++] = this.byteToBase64((b1 & 0x03) << 4);
            chars[index++] = this.c3;
            chars[index++] = this.c3;
        }
        else if (d == 2)
        {
            b1 = bs[n] & 0xff;
            b2 = bs[n + 1] & 0xff;

            chars[index++] = this.byteToBase64(b1 >> 2);
            chars[index++] = this.byteToBase64(((b1 & 0x03) << 4) + (b2 >> 4));
            chars[index++] = this.byteToBase64((b2 & 0x0f) << 2);
            chars[index++] = this.c3;
        }

        chars.length = index;
        return String.fromCharCode.apply(null, chars);
    };

    Cyan.Base64.prototype.base64ToByteArray = function (base64)
    {
        base64 = base64.replace(/\s/g, "");

        var length = base64.length;
        var n = length / 4;
        if (n * 4 != length)
            throw new Error("String length must be a multiple of four.\n" + base64);

        var bs = new Array(n * 3);

        var index = 0;
        for (var i = 0; i < length; i += 4)
        {
            var c1 = base64.charCodeAt(i);
            var c2 = base64.charCodeAt(i + 1);
            var c3 = base64.charCodeAt(i + 2);
            var c4 = base64.charCodeAt(i + 3);

            var b1 = this.base64ToByte(c1);
            var b2 = this.base64ToByte(c2);
            bs[index++] = ((b1 << 2) + (b2 >> 4)) & 255;

            if (c3 == this.c3)
                continue;

            var b3 = this.base64ToByte(c3);
            bs[index++] = (((b2 & 0x0f) << 4) + (b3 >> 2)) & 255;

            if (c4 == this.c3)
                continue;

            var b4 = this.base64ToByte(c4);
            bs[index++] = (((b3 & 0x0f) << 6) + b4) & 255;
        }

        bs.length = index;

        return bs;
    };

    Cyan.Base64.base64 = new Cyan.Base64();
    var getBase64 = function (c1, c2, c3)
    {
        if (!c1 && !c2 && !c3)
            return Cyan.Base64.base64;
        else
            return new Cyan.Base64(c1, c2, c3);
    };
    var byteArrayToBase64 = Cyan.Base64.byteArrayToBase64 = function (bs, c1, c2, c3)
    {
        return getBase64(c1, c2, c3).byteArrayToBase64(bs);
    };
    var base64ToByteArray = Cyan.Base64.base64ToByteArray = function (base64, c1, c2, c3)
    {
        return getBase64(c1, c2, c3).base64ToByteArray(base64);
    };

    var stringToUtf8Bytes = function (s)
    {
        if (s == null)
            return;
        var length = s.length, c;
        var bytes = [];
        for (var i = 0; i < length; i++)
        {
            c = s.charCodeAt(i);
            if (c >= 0x010000 && c <= 0x10FFFF)
            {
                bytes.push(((c >> 18) & 0x07) | 0xF0);
                bytes.push(((c >> 12) & 0x3F) | 0x80);
                bytes.push(((c >> 6) & 0x3F) | 0x80);
                bytes.push((c & 0x3F) | 0x80);
            }
            else if (c >= 0x000800)
            {
                bytes.push(((c >> 12) & 0x0F) | 0xE0);
                bytes.push(((c >> 6) & 0x3F) | 0x80);
                bytes.push((c & 0x3F) | 0x80);
            }
            else if (c >= 0x000080)
            {
                bytes.push(((c >> 6) & 0x1F) | 0xC0);
                bytes.push((c & 0x3F) | 0x80);
            }
            else
            {
                bytes.push(c & 0xFF);
            }
        }
        return bytes;
    };

    var utf8BytesToString = function (bs)
    {
        if (bs == null)
            return;

        if (bs.length == 0)
            return "";

        var chars = [];
        var index = 0;

        while (index < bs.length)
        {
            var b = bs[index];
            if ((b & 0x80) == 0)
            {
                chars.push(b);
                index++;
            }
            else
            {
                var n = 0;
                var x = 0x80;
                while ((b & x) != 0)
                {
                    n++;
                    x = x >> 1;
                }

                var c = (b & (1 << 8 - n - 1) - 1) << 6 * (n - 1);
                for (var i = 1; i < n; i++)
                {
                    var b1 = bs[index + i];
                    c |= (b1 & 0x3F) << 6 * (n - 1 - i);
                }

                index += n;
                chars.push(c);
            }
        }

        return String.fromCharCode.apply(null, chars);
    };

    var rsaEncrypt = function (data, exponent, modulus, bt)
    {
        if (bt < 0 || bt > 2)
            return;

        if (data.length == 0)
            return [];

        if (Cyan.isString(data))
            data = stringToUtf8Bytes(data);

        if (Cyan.isString(exponent))
            exponent = base64ToByteArray(exponent);

        if (Cyan.isString(modulus))
            modulus = base64ToByteArray(modulus);

        if (bt == null)
            bt = 2;

        if (bt != 0 && bt != 1 && bt != 2)
            return;

        var keySize = modulus.length;
        var size = keySize - 11;

        var n = Math.ceil(data.length / size);

        var result = new Array(n * keySize);
        var bs = new Array(keySize);
        bs[0] = 0;
        bs[1] = bt;

        var x = 0;
        for (var i = 0; i < data.length; i += size)
        {
            var m = size;
            if (i + size > data.length)
                m = data.length - i;

            var p = keySize - 3 - m, j;
            for (j = 0; j < p; j++)
            {
                var b;
                if (bt == 0)
                    b = 0;
                else if (bt == 1)
                    b = 255;
                else
                    b = Math.ceil(Math.random() * 255);

                bs[j + 2] = b;
            }
            bs[p + 2] = 0;

            for (j = 0; j < m; j++)
                bs[j + p + 3] = data[i + j];

            var bs1 = modPow(bs, exponent, modulus);

            var l = bs1.length;
            for (j = 0; j < keySize - l; j++)
                result[x++] = 0;

            for (j = 0; j < l; j++)
                result[x++] = bs1[j];
        }

        return result;
    };

    var rsaEncryptToBase64 = function (data, exponent, modulus, bt)
    {
        return byteArrayToBase64(rsaEncrypt(data, exponent, modulus, bt));
    };

    var rsaDecrypt = function (data, exponent, modulus)
    {
        if (data.length == 0)
            return [];

        if (Cyan.isString(data))
            data = base64ToByteArray(data);

        if (Cyan.isString(exponent))
            exponent = base64ToByteArray(exponent);

        if (Cyan.isString(modulus))
            modulus = base64ToByteArray(modulus);

        var keySize = modulus.length;
        var size = keySize - 11;

        if (data.length % keySize != 0)
            return;

        var n = data.length / size;

        var result = new Array(n * size);
        var bs;
        if (n > 1)
            bs = new Array(keySize);
        else
            bs = data;

        var length = 0;
        for (var i = 0; i < data.length; i += keySize)
        {
            var j;
            if (n > 1)
            {
                for (j = 0; j < keySize; j++)
                    bs[j] = data[i + j];
            }

            var bs1 = modPow(bs, exponent, modulus);
            if (bs1.length == keySize)
                return;

            var p;
            if (bs1.length == keySize - 1)
            {
                var bt = bs1[0];
                if (bt != 1 && bt != 2)
                    return;

                p = 1;
                for (; bs1[p] != 0; p++) ;
                p++;

                if (p < 10 || i + keySize < data.length && p > 10)
                    return;
            }
            else
            {
                p = 0;
                var l = keySize - bs1.length;
                if (l < 11 || i + keySize < data.length && l > 11)
                    return;
            }

            for (j = p; j < keySize; j++)
                result[length++] = bs1[j];
        }

        result.length = length;
        return result;
    };

    var rsaDecryptToString = function (data, exponent, modulus)
    {
        return utf8BytesToString(rsaDecrypt(data, exponent, modulus));
    };

    Cyan.Encrypt = {
        stringToUtf8Bytes: stringToUtf8Bytes,
        utf8BytesToString: utf8BytesToString,
        byteArrayToBase64: byteArrayToBase64,
        base64ToByteArray: base64ToByteArray,
        rsaEncrypt: rsaEncrypt,
        rsaEncryptToBase64: rsaEncryptToBase64,
        rsaDecryptToString: rsaDecryptToString,
        rsaDecrypt: rsaDecrypt
    };

    window.BigInteger = Cyan.BigInteger;
})();