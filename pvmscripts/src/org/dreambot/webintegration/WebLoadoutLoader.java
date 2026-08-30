package org.dreambot.webintegration;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.webintegration.loadoutmodel.WebEquipmentLoadout;
import org.dreambot.webintegration.loadoutmodel.WebEquipmentLoadoutItem;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * I'm letting people customise there loadouts but the gear picker is on web because react is much nicer than swing
 * the model exported from the web picker (https://mymule./loadout) is different than the model
 * in the script, so wwe cant directly deserialize, we do some intermediary stuff here to add conditions and such
 */
public class WebLoadoutLoader {
    public static String testCase = "{\"hat\":[{\"id\":1163,\"name\":\"Rune full helm\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACI0lEQVR4Xt2X/0vCUBTF9xhDDEmikWTYLLGYQkuxEhIrSiqJir5RghCBEP3W///DjXPHna9pP9Xbgy5cniLbPpxz7h06zv8uRdO2WjFE3bujNbdHy07DJpiihjdmmMAdMFDRqdOCUybPWbQBFQNte49Uc6/Jd6MECL/tVqvc6asMlqLQe0nsAkwaKH2F4VKJOiW3nQDlHd8eENSpuMccZrR1IKiz6h7QohNwhnBKoK0AYboAgtxIjiwBTRdhJ/fJ07bhXiZ7KOMJi60CgMDo1mWoTqyIwMgy3PEmPP4ZB1pR6+Kedk6vKOydUX2/TxutLlWabSpvNXn89ewYBlK0fzOi9vCRosEtNY+HtNU9oVqnR0G0x0AY/4wWoqLDpzc6uBtT5+p5RiUBWiqvs2WGgRQdjT+o9zyh7sMrQ4lKjf45q6QD5QtGx10xCLo/emcwNKB02wBUqoVULJUZyMsvMFT6br8sxQ/O53KcHTS+o/FZbINlCPVKUJ9R6I9VUpwXAMEinFBFGnYhQwg17AKQX9k0aVk8VToQVMEJZXDqEwbLACV2GQUSpQQICuFEfvQ9BCijQJKhtHUCJkBrYfRNJeNAPyklO0gHMqgQKoZKA4lSMvICZDhDUvHLdB4YgJAfAGUQar0Ub2lYhXGHVTJhujoFv5QdEN5lAJLtLNMl6mBLi5oGluK8UpwdealCIYFJg2QAI6X/d5+2BZBp6Q+3CpJVfQE6KPmzOny08QAAAABJRU5ErkJggg==\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"defence\":40}],\"tradable\":true},{\"id\":1161,\"name\":\"Adamant full helm\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAB+0lEQVR4Xt2XQUvDQBCFs4QQItJSDJRaarSllVRpa6m2hYIKigiiKHjw4NEf4P8/jLxZJl2WiAfdXfDBsNCS5OPNmwmJov8tRdsKKg0xTF6oG29oLxqHBFM0Tt4ZpohvGagZDWkn2qckaoSA0kDHySsN4kfK42kFhP9mh4dc9lUOpahM3qp2AcYGsq9wLFW5047nFVAW5eGA4E4vvuIwo4IDwZ1OvKRGVHCGcEqggwBhugCC3EiOAgFtF+Ei/eRpO4rvqz3kecJ0qwAgMGbrPLqjHREYWYaT5IPH33OgFU0upzRej6lcljQ4G1BxUlB32KVOv8Pjb2bHMZCi+c2cZtczOt2cMtBoMaL+pE+98oCBMP6eFqKi1cOKFnfnDCUuAUhcAlCr0+KWOQZStHne0PppTRf3FwxlumQDZY3MJZBiECmAoQBlugSgdtGmZt6kNEu5dPj/VIodydKUAaTwm7QNLsEhhDrv5hWQo1DrEAMILcIJCAGRUJvtQplA9h1/KcWO2EA4AYRTJgztgkMoL0B1TuE0xx5QKKdAkqHvgOzFKE45B/rOKQDBHQAJjEOHIA31E5DpjmMgSL9M68CQIbtdHoAg/epAdsztbLdrt7XrDwibGq3Cdq4LM5aiuOlgKdZJVUCyEMUdG8QDjMj8dt9WAJCtzIcHBfGlL1sa40v6pgoWAAAAAElFTkSuQmCC\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"defence\":30}],\"tradable\":true},{\"id\":1159,\"name\":\"Mithril full helm\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACFElEQVR4Xt2Xb0sCQRDGbzkOMSSRJElRM7lEC/9RahhRUEQREYVQQRBBH6Dv/2LqmWXOZbFXubvQwLCI3N2P53lmjoui/12Klh20NESaPFItPqWtqBcSTFEveWOYZnzFQMUopY2oSkm0GQJKA3WSBbXjeyrHgwwI/w13d7ntqxyWom7yktkFGBvIvsJxqUydSjzOgPJRORwQ1KnH5xxmdHAgqLMTT2kzanKGcEqggwBhugCC3EiOAgEtF+FR7ounrRXfZHvI84RpqwAgMKZ1HtXRigiMLMN+8snj7znQig4Ob6nXu6b9zgW126fUbB5TrTagSqXL429mxzGQotHoifr9O4bqdi8pTc+o1Zox0PZ2yuPvaSH+hPb4mcbjBQ2GDwwFlQSo3hgzULFYZcscAymanbzTdPaWQQmQ2GYC5fNOx13RZPLKDSCAzecfDCW2mUCFQoWSZINbh3+tpfjB+VyOs4PGb7RkCSrBsmr1gEqlRgbkKNSK8wIgPBwnVDGnDBnClGHCAATLTCD7jn8sPVU2EE5YhVNGHnaVy3sM5QVIlBIgKITT3EMC5RRIMrTKOpwy8sgQbBMo50C/KSVLEUAeFEJpKBtIlBIgUx3HQCj9Ml0FhgzZdnkAQulXB6zCuEMZCbNpl72H7LussfTGhlXmdjbVAYyo6WApriq1EggwNogHGCnz233ZAUCWZT48KIiv+gbya+YpJcmtPQAAAABJRU5ErkJggg==\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"defence\":20}],\"tradable\":true},{\"id\":1153,\"name\":\"Iron full helm\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACFUlEQVR4Xt2XX0tCQRDF73K5SBJFKImKmomFGlpiZdgfw6ysKCp66KHHPkDf/2HizDL3Los91e5CA8Micu/+OOfMiFH0v0tR1kFLQ7STF6rGp1SIuiHBFHWTD4ZpxHMGWo/alI8qlERrIaA00G7yRq34kYrxIAXCd/tbW9z2Uw5LUSd5T+0CjA1kP+G4VKpOKR6mQCtRMRwQ1KnFUw4zOjgQ1CnHx7QWNThDOCXQQYAwXQBBbiRHgYCyRTjKffG0NeNFuoc8T5i2CgACY1rnUR2tiMDIMuwnnzz+ngOt6Hw8ptOjIzo+OKBhv097nQ7ttlq0Xa/z+JvZcQyk6PrigqaTCUOdjEZ0uD+gQa/HQI1qlcff00JUdHc1o9vLKc3OzhgKKglQd2eHgTaLRbbMMZCi58WCnm5uUigBEttMoNV83iWQoof5nBtAAHu9v2cosc0EKmxs0Eoux63D/6el+GK8HNlB4zNasgSVYFm72aRyqZQCOQq14rzgAlyOE6qYU4YMYcowYQCCZSaQ/cZflp4qGwgnrMIpIw+7apUKQ3kBEqUECArhNPeQQDkFkgwtsw6njDwyBNsEyjnQT0rJUgSQB4VQGsoGEqUEyFTHMRBK/5guA0OGbLs8AKH0TweswrhDGQmzaZe9h+y3/GHpjQ2rzO1sqgMYUdPBUlxWaikQYGwQDzBS5n/3rAOAZGVeHhTEV30DJknstrLujw4AAAAASUVORK5CYII=\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"defence\":1}],\"tradable\":true},{\"id\":1155,\"name\":\"Bronze full helm\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAB0UlEQVR4Xt3XS2vCQBAH8CxLCBGJiGKJUh8VKyqoSB8UWvAitBR6KPTQQ4/9AP3+hyn/XSaZrt7a3YUODEEk5sc8Npgk/zsU1Rk1LGKWvtJA31MnWcaEKVqmHwYz0gcDaiUzaiR9SpMiBsqC5ukbTfULdfWmAuG77Xhs0r3LYyhapO9Vu4BxQe4dnkNV1TnTuwqUJ914IFTnXO/NMCOjg1CdUt9SkYzMDOHKAx0FhO0CBHPDcxQJVB+EV9mX2baJfqrOocAbZlsFAGNk6wJWx1aEMXwYrtNPs/6BB1rRzWWHdtM2bSYtWg0Lmg8KuiibNOw1zPrL2fEMUvSw6tHdonuEYhDWP9CBqOiwLWm/PqtQbpUA6rUy0zLPIEXP13163JUVSlbJBTVz7RNkK4MEiBMo2TaA+p2c2s3UgBjl/tovQ5kH51lm2oTEZ9k2oHiGUB0GeRpqO8QA4eG4AsApq8Pt8tyyY5CE4crVQbuQwUAujEFcIYnyCpIzdKpScsOQjAoGcmHyUGSQxwohLMqFRQQh7MuU113CMEMMCjBDMuwBya8M+VKVIPcccn/lD+MnSG6XxHA1PRyKp0KdBPFWSUgADIf8715nBEgd8uFRIaHiG9J9z+xcBjkRAAAAAElFTkSuQmCC\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"defence\":1}],\"tradable\":true}],\"neck\":[{\"id\":1706,\"name\":\"Amulet of glory(1)\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACbElEQVR4Xu2XbUtTYRjHvYhxmOxD+BH6FhbCssx06ULDyswHzGwZuJbZwNZ6sAezsSFiU0Y41jIZG6RTghGj6IUxohf5oi9h8G/3fTpn8xyr+y7PGUR/uNhg57rP71wP57pWV/df/64IvzZbRViYeYznc3NYSSSQfZFCPpvF280N3LkewGmPx04wwvyjh3gaCuHejQnEZ58gk0zi09YWvu3s4HOphPTSIoJjY+hqa7MDjLAYiSA2fZ9/mlOl2odikcNO+nw4daLVSjDiqWKpYd8PNjTsaRrAeibD0ztxeRSdx1v2G4p4vbCU+C8O60DGqzRVg0XCYTgV5bc+kiKsrb4yRcd41W4R3hUKkj7CIqTicRxpbJR4UsJmLseLXNxHWPQHT0pYXV5G4NKINUA9HR2SBxOSzxZwdXBQ0k9IpL9bxA8mJGIxjPT2SvoJifTWFT+Y+Pto6EyPpJ+QCCePHZU4mJB7mcbta37dR8xPWIRWt1sKiEXH198v4SMlQktTk+DhanSmb05aFR0mwoNgUBhoKRq1qt01EQr5db1jjL9WpI4LthFYGB0m4isGm/Rnvd4fUGZjUWQv0Fvj41ZGRxPxZYzdtL25GZ52B7q7HDh/zoHRvj4+dNnKYWFnGUX4uv0Fw0MKrvhUG7igQrF6YTNLfrz8lQh3w06EphQE/ArcjgFu3k4HTxHbIm2FiUacOkS1sdRpNWQTDFN5uZ/fG4jZ4UMH7CjiahFW0vUmkGqzHejNhssEUVOg0kcX8ms/j5LNQEzlvzjvVahUsp7X1OxMpetqAMRUXtyLLrzOVaBq0GFG7R4XGsh+w3wHakMvAaXE5WMAAAAASUVORK5CYII=\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[],\"tradable\":false}],\"ammo\":[{\"id\":-1,\"min\":1,\"max\":1,\"refill\":1,\"name\":\"placeholder\"}],\"weapon\":[{\"id\":1333,\"name\":\"Rune scimitar\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABxklEQVR4XuXWMUvDQBQH8B6lhBRnF5eWoqCCsVKaDqUoKkopKFWhCIKCg+LkpIiCgyiCIBRFEIQOgoMgOAhufgG/09N34TWXd5kkuSv4hyOkacqP9/IuzWT+ZwTwTywFIQImZ5vyyK8aTACZaCxJjLe4Ks+nCwXTqAAyVpuLYGaaGzZAAkarDQ1TXl6DSqtjGhRixusLGsZvb5kE6Zip+VYEU1vfNgUSUKrU+xhsFWKoOvjsIKq6smkCFGCwOoTB6tCKg6U4+gKK5ZrWqrilwlJCBRjeKg7Bz/E6bQW0sG0Jti7E8BGnhecqhADq4r/6x+gY3iq1KikA1MRjqDp4xHNeFf4rCSacKD7iKiblqlB0DE1PHIbfnXCiGPUdhUeDLcLoGNpTCGOoRRgBvUs3sgvT+4lj+J0pRcB+Jwe3Ry48X7u2MRgBvpeFnXYOzg8cuD9z4eXGtYXB/P6lGM6AN5IFv5iF410H3rr5Poh/20CExNRLAQjb9/FgDSRki75fhySGRttSuwRcHTrwfpeHr15YEYPjrUbIZwWrgyNv+ZnBCDjdc+SoP124skoDA8K95/NxQEDdk2C/sfQQ8+j/8vg3jMc25Ac46tbIdl97EgAAAABJRU5ErkJggg==\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"attack\":40}],\"tradable\":true},{\"id\":1331,\"name\":\"Adamant scimitar\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABmUlEQVR4XuXWv0vDQBQH8DxCCSnOLg6tLRZaSxNLbRAKIgji6uQqOChOToooOIgiCEJRBEHoUHAQBAfBzX/A/+nJu3Bt+pooleRdwS8cIT/58N7dEcv6nwHkVwyFIIC1lZo68ruCCSHVoKowjVVPnfvFojQqhFRalQGm3qmjv+abAAGWl8qxmOZ6Uxr0M6a1sSwJAiw1SgoUxdC80Zj2ZlsKNMTw6hBIuEKAhcXCGCY6OCzDpR9ieKvihgBqFJNUHbpG9/RWoAe1LcXW/Y7hEA2IDv7VP2YyTAaAaJIxNEeSqsK/kmKSl7dgVXQmw/C3U04yho6CLaLEY2hP0RihFlEAe5fuCEZvcBzD38wogPvbObw9crF/7ZrGUAADz8adrRyeHzh4f+bi841rCkMBXJi10JuzMZi38XjXwddufgDiTwsEFKZTDkHUvvcHYyBQLfp6mVEYvbQNtQvw6tDBt7s8fvaGFRFc3tGAmitUHVryhucMBfB0z1FL/enCVVWaGhDtPR+PUwLqnoT7jaFJzDP+l8efEI9pyDdmqcA4rHbzbQAAAABJRU5ErkJggg==\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"attack\":30}],\"tradable\":true},{\"id\":1329,\"name\":\"Mithril scimitar\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABvUlEQVR4XuXWz0cEYRgH8HmsNWZ17pLaHWPLbtlfWtthRUSkQ0rsNTqUTp1Kig4pEbE2EbGH6BDRIbr1D/Q/PXne8c6888ykZOZ9l768xvz08TzzzK5l/c8A8iOGQhDASmVZbPlZjfEh0zNLAjM7tyr2G6WSbpQP8bxuBFOrrZkAAbruQgRTra4ITLO5qRsUYsrlxQim3ljHVmtLJ+hnzHy7pwsEWCy2Awy1ijDyvSEQtUtThXwMVUdiqDpyJcEyHH3AyalWrFVJi8MyQPkY3ioOoeN0Xn4K5KK2pdi6EMNHXC7aVyESoC7+1D8mjuGtUquSAUBNMkZWh7a0z6vCn5JiwoniI65iMq6KTBwjpycJw+9OOd9jaKuxRZQoRv31lhhNLaIADi+cX2H4nRkFcK+Xx5tDBx+vnOBrawhDAezUc7i9kcezfRtvTx18unZMYSiA5XEL6xM57Lg5PNqx8aVfCED8ag0Bgel6Poja93ZnDASiRZ/PYwIjR9tQuwAvD2x8HRTwYxhWRON4qwHxrlB1aOQNvzMUwJNdW4z6w7kjqjQyIPr2vN+PCKh/7H9vDL3EPPF/efwK7TEN+QJNTsO4YMBl2QAAAABJRU5ErkJggg==\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"attack\":20}],\"tradable\":true},{\"id\":1323,\"name\":\"Iron scimitar\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABvklEQVR4XuXWMUvDUBAH8B6hhBRnF5eKWKiVppXaVtraSkXE0clVcFCcnBRRcBBFEISiCILQQXAQBAfBzS/gdzq9F17ycokokrxX8A+PkKQJP+5yaTKZ/xlAfsRQCAK4UKuJLT+rMR6kUa0KzGKzKfYr+bxulAeZK5dDmF6rZQIE6JZKIUy7XheYlW5XNyjAzFfcEKbf6eDqUk8n6GfM2nJfFwhwtlj0MdQqwsjnhkDULk0V8jBUHYmh6sgVB0tx9AFnCoVIq+IWh6WA8jC8VRxCx+m8fBXIRW1LsHUBho+4XLSvQiRAXfyuf0wUw1ulViUFgJp4jKwObWmfV4XfJcEEE8VHXMWkXBWZKEZOTxyGX51wvsfQVmOLKGGM+u8tMZpaRAEcnjm/wvArUwrgzkYWr/YdfLhw/LetIQzl62vPtXBzPYsnuzbeHDv4eOmYwlAAp8cz6E5Y2Ji08GDLxudBzgfxX2sICEx7ygNR+15vjYFAtOjjaUxg5Ggbahfg+Z6NL9c5fB8GFdE43mpAPCtUHRp5w88MBfBo2xajfn/qiCqNDIjePW93IwIaHHrvG0MPMU/0K4//QntMQz4BiIfKlOIFvd0AAAAASUVORK5CYII=\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"attack\":1}],\"tradable\":true},{\"id\":1321,\"name\":\"Bronze scimitar\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABjklEQVR4XuXWwUrDQBAG4AyhhC2lIkqlFKS1qEjBFA8WQcGLL+DJq+BB8eRJEQUPogiCUBRBKPQgeBAED4I3X8B3GpkNazfT2FTJ7hb8YSltk/AxM7vE8/5nAPkvjkIQwKX6uPzk/1pMBAlrYxKzPDchvzerVduoCNKYLsYwKwuTLkCA85ViImatUbINGoxZX5yyCUrHbDTLtkBxjA7SUZZa1sPo1dEXhxnc+hGGt+qnZRgFOFMupGJUC9VRoBa1LcPW9WOSQDpEAfTFn/rHxDFJ1dGrYgCgZzgMrwp/SoZJHmIaVh1juCoqv8PwuzPOYIzFFlHSMZZaRAHsXoihMPxOQwHc28rhzaHAxyvhGkMBbIU+bm/m8Gw/wLtTgU/XwhWGAjhb8jCs+Niq+Xi0E+BLO/8N4ldbCEjMaj0CUfve7p2BQLbo87kgMWprO2oX4OVBgK+3efzo9ipicXvrATkrVB3a8o5nhgJ4shvIrd45F7JKIwOis+f9YURA7ePovHE0xDz9b3n8CutxDfkCwKyp2ceD3VgAAAAASUVORK5CYII=\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[{\"attack\":1}],\"tradable\":true}],\"chest\":[{\"id\":-1,\"min\":1,\"max\":1,\"refill\":1,\"name\":\"placeholder\"}],\"shield\":[{\"id\":-1,\"min\":1,\"max\":1,\"refill\":1,\"name\":\"placeholder\"}],\"legs\":[{\"id\":-1,\"min\":1,\"max\":1,\"refill\":1,\"name\":\"placeholder\"}],\"feet\":[{\"id\":-1,\"min\":1,\"max\":1,\"refill\":1,\"name\":\"placeholder\"}],\"ring\":[{\"id\":-1,\"min\":1,\"max\":1,\"refill\":1,\"name\":\"placeholder\"}],\"cape\":[{\"id\":21295,\"name\":\"Infernal cape\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC/ElEQVR4XuWX7WvTUBTGl5uCdN2avtdVVjqrG10HrQw3sbIhdasdWOZAmDLc2Nhw6oYfBN/aBAU/C4J/7/E89+YmsYofJMkGHjik93br+eV5zklvJyYiDYP8vPAwaKnRoOvVKs2Uy5cBygfC63atJnP8r2IMBfQ7zIVYaFCpUKCFet0D0vtTk5M0W6nECWXQQ4YBUBBKJywM2jj+3yGHQVY6LfMZw6Dwzbk5ut1uUavZlBb+2cZIwqDzTIZG9wVl2Ja0C4YE1OL8fLwwo5ZS5wkr82FL0LGVprOsRRnLohvZLH09T8QHY3cEfXogaCaZlMog7U0hYXTGAKOa1N41aYWtgjrPWZ3hXUEFBtsrlejzPUFW9DAKpMpFB/k82fum1yvIDYaz+4KGDPTl2CRnz4xqohSI0xP0MmPRSfWKvHtki+E00GNevyilvPciUsegbMov4nDTOs0k1XmasD4tFmmD30cPneayNIgaRkL0VD/I1221B5BNBnG4qVts1S6vnbMEOTuCigwbgVWqMHIHxbgfAOZ0hafYAe93U0opT72BCFsd1S/vSkVZ3FnlXBfyzq+5hY8YpMMqQAkAaCAJFQAa/+R/CDVB+GC7NkULXLDL0/QWcKzQL4VXk+QsGxLAOTKldQAPUR23X7hHoArswFUDfLxaloBBeyruGoqFDINwe4bH9xAFTviun5pev9xhpaQSrArAAATLPMB6OkyrEAb9GKhnCp6++1ykAwhuZNh4xHt4BmEPE+a8Scge01Ahq4MwKM9fhLjiLLPmFirg7peUjVqNKvYOTKVUL3SrgmFQP2lKsOGKoFn3KbzMa1ikgaRFzbTXUyFbFQx1jAAQvrVfsVI449h8vMCUrXM/rWLyUmrcYVmE6iACQNPT1OC+ef9IUJOvr7lv7EOTthgKU4WeihgG4QN96yfp+y1lGXoK17VcjkbbvnURWqXDPxePJ34xLOKYymeeEfcX9mIAQgR/AvsJ1fQU6ozYLj90oWCOQ8QG87e4FBD/dfwETA8jP5Ky+JIAAAAASUVORK5CYII=\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[],\"tradable\":false},{\"id\":6570,\"name\":\"Fire cape\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADIElEQVR4XuVXTWsTURTt4N5NtTaNGQPJohWLpRRsF1nERQQrZGGg3WSRRYWKoIgK7twoqTBCQ2b+7u095747mcGKQl+ahRcu89J8vDPnnHvedG3t/6pErH290kpkP73D9vUKQRkzDmKv3V4VKANy+XpLpocbkr/dqQBavL94vdRSIMcNmR6sSz7uyGyYyq+XTZmfPqr56BZYsrvORx0p3u9K1nsgWX+TazRYmiu462WMXmAllfzdY7IDNooPe3yNno1aZKv4/FSKj/s1k0cGFFgZ7ygDu9wMEoGZfNKVy8GmzLA+78rPfoMgyRZY1HVkQEEeBZG/2Zbi65FtGEBhPR+l8u3onlwct6T4dGCt7097WyWYSIAUzGmHzU0ARK/5ZJsg6JexyTY7adHUYMwBRQdTvVtsCv+gIYd7Caamj+AdfA5S6TpTViNKlZhEygCMOnuRkiWsmTdnXTaNPWmbyTWLwBoA4fMR2bF8cXnQMC3zRuXwvAELBIYJ86t2RKlskghC75J3CyngH70ybyBPyBpKqEBdLgeEdQSpEttIGciHBoAegWTDIIXnjY42jEzPhGnDdyATJI3ATsJkLacDraNNFuAH3YR5o7LMX7XJiucNJaqwiOsN2THzluN8Zh5xcGRLr/ybMgJmmDeD5sJjev3eb0r2PBI7AJENWra5gsPaRxns+PRU84bsgBWEZAAMFm/IDiqhNGDkx5O7xoQCRMKW59TIDFvmDdgDq6EZByeR2OE0hfjHqAMUmACYbPiQR4Vni5saJueRgaNDjQzwkCwKO3x8GDRKUL4hAYTxLzMJ7weje97gewAXgR0vyx6Cgk/0xzlFuiF84kyU0oTR9rwhUAUYgZ1qmbHx45ALRwOmiMdBlR2YOJxhzCF4qQImKiCyo2eST9ZvjxI+2jqBPOUhWfBVZHZQiaVxAFSbIoD58swMrPJBxuzwfu0RNTI7KJPMzyJsTJPDNzryOEjB1rS3zvf9Id6BRAbjFSIAjxLn5g16So8KyARm4CuE5JJYua783+BF+4ijlyjRn6sug51zMPEtSPSvtQJW/lbLBnMFtCSEpE43WwYAAAAASUVORK5CYII=\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[],\"tradable\":false},{\"id\":6568,\"name\":\"Obsidian cape\",\"icon\":\"iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABS0lEQVR4Xt3XQYrCQBAF0DQiIUGcAwgqnsAjeAIXgotZuPD+h+iZSvGTn7INLqyKWFB0RDGP39UYq+r7K+WhZ6+Uf9tl17f/nhmW8rlZEEL7vgIsvFK+NHrz437ftVwjKX0dWin/rNeFbdLXwSDFcAMi83Tt5ymkFHPYbnvMqm375oQCUkodBJimrkcghoRi0AKSVTC7zSZydoaZAYQ7GPQ4wNwCEoysAaBpDDochOHFzFgQ2hk0xtiTZWHOR33AIBVeGQKYezoWU0oH74WkwwlMwRhkv+kN9XiqkBQnxhjn7dKtsigLFIxNxwGkPw2vgpCYezo8H7w+ayeQPnqe6kXXPMgMsum5bhcwAgOOt9BupyNGagzCao88rh23ikuf9GxSDLOD7AyqKr3B8HfGbqGszierXFOwsHRKZWEMsp8NLYbNlk6pPgrjVX/LoKa2ixDIDAAAAABJRU5ErkJggg==\",\"min\":1,\"max\":1,\"refill\":1,\"reqs\":[],\"tradable\":true}]}";

    static Map<String, EquipmentSlot> slotMap = new HashMap<>();
    static Map<String, Skill> skillMap = new HashMap<>();
    // map ids in an item variant to the variant so that whatever is selected in the loadout gets supported properly
    static Map<Integer, ItemVariant> variantMap = new HashMap<>();

    static {
        slotMap.put("hat", EquipmentSlot.HAT);
        slotMap.put("cape", EquipmentSlot.CAPE);
        slotMap.put("neck", EquipmentSlot.AMULET);
        slotMap.put("ammo", EquipmentSlot.ARROWS);
        slotMap.put("weapon", EquipmentSlot.WEAPON);
        slotMap.put("body", EquipmentSlot.CHEST);
        slotMap.put("chest", EquipmentSlot.CHEST);
        slotMap.put("shield", EquipmentSlot.SHIELD);
        slotMap.put("legs", EquipmentSlot.LEGS);
        slotMap.put("hands", EquipmentSlot.HANDS);
        slotMap.put("feet", EquipmentSlot.FEET);
        slotMap.put("ring", EquipmentSlot.RING);

        // not all skills need to be mapped idk of any equipment items that have like, a fishing requirement
        skillMap.put("attack", Skill.ATTACK);
        skillMap.put("defence", Skill.DEFENCE);
        skillMap.put("magic", Skill.MAGIC);
        skillMap.put("ranged", Skill.RANGED);
        skillMap.put("agility", Skill.AGILITY);
        skillMap.put("strength", Skill.STRENGTH);
        skillMap.put("prayer", Skill.PRAYER);
        supportVariants(
                ItemVariants.AHRIMS_HOOD,
                ItemVariants.AHRIMS_ROBEBOTTOM,
                ItemVariants.AHRIMS_ROBETOP,

                ItemVariants.AMULET_OF_GLORY,
                ItemVariants.RING_OF_DUELING,
                ItemVariants.RING_OF_WEALTH,

                ItemVariants.DHAROK_CHEST,
                ItemVariants.DHAROK_GREATAXE,
                ItemVariants.DHAROK_HELM,
                ItemVariants.DHAROK_LEGS,

                // todo all other barrows
                // todo imbuded rings

                ItemVariants.ARCHERS_RING,
                ItemVariants.AVAS_DEVICE
        );
    }

    private static void supportVariants(ItemVariant... itemVariants) {
        for (ItemVariant v : itemVariants) {
            for (int id : v.getIds()) {
                variantMap.put(id, v);
            }

        }
    }

    public static EquipmentLoadout parseEquipment(String json) {
        Gson gson = new Gson();
        WebEquipmentLoadout webLoadout = gson.fromJson(json, WebEquipmentLoadout.class);
        EquipmentLoadout equipmentLoadout = new EquipmentLoadout();
        // parse web loadout into actual loadout
        for (Field field : WebEquipmentLoadout.class.getFields()) {
            try {
                field.setAccessible(true);
                WebEquipmentLoadoutItem[] items = (WebEquipmentLoadoutItem[]) field.get(webLoadout);
                String slotName = field.getAnnotation(SerializedName.class).value();
                Logger.info("Field " + slotName);
                EquipmentSlot slot = slotMap.get(slotName);
                if (items == null || items.length == 0) {
                    Logger.info("Skip slot " + field + " " + items);
                    continue;
                }

                Collections.reverse(Arrays.asList(items));

                for (WebEquipmentLoadoutItem item : items) {
                    if (item.id <= 0) {
                        Logger.info("placeholder id when parsing loadout");
                        continue;
                    }

                    // todo here we need to consider if the id is part of an item variant
                    ItemVariant variant = variantMap.get(item.id);
                    if (variant != null) {
                        Logger.info("Variant found " + variant);
                        Logger.info(slot + " add item(variant) " + item.toString());
                        equipmentLoadout.addItem(slot, new EquipmentLoadoutItem(variant)) // i dont think theres any variant stackables
                                .setRefill(item.refill);
                    } else {
                        Logger.info(slot + " add item " + item.toString());
                        equipmentLoadout.addItem(slot, item.id, item.min, item.max)
                                .setRefill(item.refill);
                        // if its not tradable (the on the GE) we only use if its owned, for things like fire cape
                        if (!item.tradable) {
                            Logger.info("Parsed not tradable " + item);
                            equipmentLoadout.enabledIfOwned();
                        }
                    }


                    // todo parse requirements here for progressive loadouts
                    if (item.requirements != null && variant == null) {
                        for (Map<String, Integer> reqMap : item.requirements) {
                            for (Map.Entry<String, Integer> req : reqMap.entrySet()) {
                                if (!skillMap.containsKey(req.getKey())) {
                                    Logger.error("NO MAPPED SKILL FOR _ report to camelCase NOW!!!!" + req.getKey() + " " + req.getValue());
                                }
                                // ADD not SET, we have multiple sometimes

                                Logger.info("Adding req " + req.getKey() + " " + req.getValue());
                                equipmentLoadout.addEnabledCondition(() -> skillMap.get(req.getKey()).getLevel() >= req.getValue());
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Logger.log("E " + e);
                e.printStackTrace();
                continue;
            }
        }

        return equipmentLoadout;
    }

}